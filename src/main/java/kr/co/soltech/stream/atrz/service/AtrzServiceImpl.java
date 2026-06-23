package kr.co.soltech.stream.atrz.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import kr.co.soltech.stream.atdc.model.AtdcModel;
import kr.co.soltech.stream.atdc.service.AtdcService;
import kr.co.soltech.stream.atrz.model.AtrzModel;
import kr.co.soltech.stream.atrz.model.AtrzParamDTO;
import kr.co.soltech.stream.atrz.opnn.model.AtrzOpnnParamDTO;
import kr.co.soltech.stream.atrz.opnn.service.AtrzOpnnService;
import kr.co.soltech.stream.atrz.repository.AtrzRepository;
import kr.co.soltech.stream.cmmncd.model.CmmnCdParamDTO;
import kr.co.soltech.stream.cmmncd.service.CmmnCdService;
import kr.co.soltech.stream.commons.model.CustomExceptionDTO;
import kr.co.soltech.stream.commons.utils.SoltechStreamUtils;
import kr.co.soltech.stream.file.model.FileModel;
import kr.co.soltech.stream.file.service.FileService;
import kr.co.soltech.stream.hldy.model.HldyModel;
import kr.co.soltech.stream.hldy.model.HldyParamDTO;
import kr.co.soltech.stream.hldy.service.HldyService;
import kr.co.soltech.stream.schdl.model.SchdlModel;
import kr.co.soltech.stream.schdl.service.SchdlService;
import kr.co.soltech.stream.vctn.model.VctnModel;
import kr.co.soltech.stream.vctn.service.VctnService;
import lombok.RequiredArgsConstructor;

/***
 * 결재 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AtrzServiceImpl implements AtrzService {
	/***
	 * 확장자명, 파일 관련 도메인명
	 */
	private final String domainNm = "atrz";

	/***
	 * 결재 JPA 레파지토리 인터페이스
	 */
	private final AtrzRepository atrzRepository;

	/***
	 * 결재 의견 서비스 인터페이스
	 */
	private final AtrzOpnnService atrzOpnnService;

	/***
	 * 휴일 서비스 인터페이스
	 */
	private final HldyService hldyService;

	/***
	 * 공통코드 서비스 인터페이스
	 */
	private final CmmnCdService cmmnCdService;

	/***
	 * 파일 서비스 인터페이스
	 */
	private final FileService fileService;

	/***
	 * 근태 서비스 인터페이스
	 */
	private final AtdcService atdcService;

	/***
	 * 휴가 서비스 인터페이스
	 */
	private final VctnService vctnService;

	/***
	 * 일정 서비스 인터페이스
	 */
	private final SchdlService schdlService;

	/***
	 * 결재 목록 조회
	 */
	@Override
	public Page<AtrzModel> inqAtrz(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		return atrzRepository.inqAtrz(atrzParamDTO, pageable);
	}

	/***
	 * 결재 상세 조회
	 */
	@Override
	public AtrzModel getAtrz(AtrzParamDTO atrzParamDTO) throws Exception {
		AtrzModel atrzModel = atrzRepository.getAtrz(atrzParamDTO);
		if (ObjectUtils.isEmpty(atrzModel)) {
			throw CustomExceptionDTO.of("ERROR_0001", "docNo", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		AtrzOpnnParamDTO atrzOpnnParamDTO = new AtrzOpnnParamDTO();
		atrzOpnnParamDTO.setDoc_no(atrzModel.getDocNo());

		return atrzModel.toBuilder()
				.atrzData(SoltechStreamUtils.convertResultMap(atrzModel.getAtrzData(), "yyyy-MM-dd", "HH:mm:ss"))
				.atrzOpnnList(atrzOpnnService.inqAtrzOpnn(atrzOpnnParamDTO, null).getContent()).build();
	}

	/***
	 * 결재 등록/수정
	 */
	@SuppressWarnings("unchecked")
	@CacheEvict(value = { "atrzCnt", "atrzCmptnCnt", "atrzUnCmptnCnt" }, allEntries = true)
	@Transactional
	@Override
	public int upsertAtrz(AtrzModel atrzModel) throws Exception {
		if (ObjectUtils.isEmpty(atrzModel.getDocNo()) || "0".equals(atrzModel.getDocNo())
				|| !atrzModel.getDocNo().matches("^\\d{4}-\\d{4}$")) {
			String year = String.valueOf(LocalDateTime.now().getYear());
			atrzModel = atrzModel.toBuilder().docNo(getMaxDocNo(year)).build();
		}

		List<FileModel> fileModelList = new ArrayList<FileModel>();
		if (!ObjectUtils.isEmpty(atrzModel.getMultipartFileList())) {
			CmmnCdParamDTO cmmnCdParamDTO = new CmmnCdParamDTO();
			cmmnCdParamDTO.setCmmn_cd_id(domainNm);
			List<String> supportExtensionList = cmmnCdService.getSupportExtn(cmmnCdParamDTO);

			for (MultipartFile multipartFile : atrzModel.getMultipartFileList()) {
				String originalFilename = multipartFile.getOriginalFilename();
				if (!ObjectUtils.isEmpty(originalFilename)) {
					try {
						String fileName = SoltechStreamUtils.changeFileName(multipartFile.getOriginalFilename(), null,
								supportExtensionList);
						String fileExtension = SoltechStreamUtils
								.parseExtensionName(multipartFile.getOriginalFilename());
						String filePath = fileService.getDefaultFilePath() + File.separator + fileName;
						File newFile = new File(filePath);
						multipartFile.transferTo(newFile);
						fileModelList.add(FileModel.builder().fileId(SoltechStreamUtils.createEncodeId())
								.dmnClsfId(domainNm).fileNm(fileName).filePath(filePath)
								.orgnflNm(multipartFile.getOriginalFilename()).fileExtnNm(fileExtension)
								.fileSz(BigInteger.valueOf(multipartFile.getSize()))
								.fileMimeType(multipartFile.getContentType()).build());
					} catch (IllegalArgumentException uoe) {
						throw CustomExceptionDTO.of("VALID_0005", originalFilename, HttpStatus.BAD_REQUEST.value(),
								new BadRequestException(uoe.getMessage()));
					}
				}
			}
		}
		Map<String, Object> atrzData = atrzModel.getAtrzData();
		if (fileModelList.size() > 0 && ObjectUtils.isEmpty(atrzData)) {
			atrzData = new HashMap<String, Object>();
		}
		for (int idx = 1; idx < fileModelList.size() + 1; idx++) {
			if (ObjectUtils.isEmpty(atrzData.get("file_info" + idx))) {
				atrzData.put("file_info" + idx, new HashMap<String, Object>());
			}
			((Map<String, Object>) atrzData.get("file_info" + idx)).put("file_id",
					fileModelList.get(idx - 1).getFileId());
			((Map<String, Object>) atrzData.get("file_info" + idx)).put("file_nm",
					fileModelList.get(idx - 1).getOrgnflNm());
			((Map<String, Object>) atrzData.get("file_info" + idx)).put("file_mime_type",
					fileModelList.get(idx - 1).getFileMimeType());
		}

		String[] requiredFileds = { "atrz_info", "cprt_info", "rfrnc_info" };
		String[] requiredInfos = { "aprvr_id", "atrz_dt" };

		boolean isCamelCase = false;
		if (!ObjectUtils.isEmpty(atrzData)) {
			for (String key : atrzData.keySet()) {
				if (!isCamelCase && key.matches(".*[A-Z].*")) {
					isCamelCase = true;
				}
				if (atrzData.get(key) instanceof Map) {
					Map<String, Object> atrzDataInner = (Map<String, Object>) atrzData.get(key);
					if ((key.startsWith(requiredFileds[0]) || key.startsWith(requiredFileds[1]))
							&& !atrzDataInner.keySet().contains(requiredInfos[0])) {
						atrzDataInner.put(requiredInfos[0], "");
					}
					if ((key.startsWith(requiredFileds[0]) || key.startsWith(requiredFileds[1]))
							&& !atrzDataInner.keySet().contains(requiredInfos[1])) {
						atrzDataInner.put(requiredInfos[1], "");
					}
					for (String innerKey : atrzDataInner.keySet()) {
						if (!isCamelCase && innerKey.matches(".*[A-Z].*")) {
							isCamelCase = true;
						}
					}
				}
			}
		}
		if (isCamelCase) {
			atrzModel = atrzModel.toBuilder()
					.atrzData(SoltechStreamUtils.convertResultMap(atrzData, "yyyyMMdd", "HHmmss")).build();
		}
		int updatedRows = atrzRepository.upsertAtrz(atrzModel);
		if (updatedRows != 0 && !ObjectUtils.isEmpty(fileModelList)) {
			fileService.upsertAllFile(fileModelList);
		}
		if (updatedRows != 0 && !ObjectUtils.isEmpty(atrzModel.getDocSeCd())) {
			if (!ObjectUtils.isEmpty(atrzData.get("prd_info"))) {
				if (atrzData.get("prd_info") instanceof Map) {
					Map<String, Object> prdInfo = (Map<String, Object>) atrzData.get("prd_info");
					CmmnCdParamDTO cmmnCdParamDTO = new CmmnCdParamDTO();
					cmmnCdParamDTO.setCmmn_cd_id(atrzModel.getDocSeCd());
					if (!ObjectUtils.isEmpty(prdInfo.get("vctn_cd"))) {
						cmmnCdParamDTO.setCmmn_cd_user_dfn_vl2((String) prdInfo.get("vctn_cd"));
					}
					Map<String, Object> mapping = cmmnCdService.getAtrzMapping(cmmnCdParamDTO);

					if (!ObjectUtils.isEmpty(mapping)) {
						String strPrdBgngYmd = (String) prdInfo.getOrDefault("prd_bgng_ymd", "");
						String strPrdEndYmd = (String) prdInfo.getOrDefault("prd_end_ymd", "");
						boolean containsHldy = (boolean) prdInfo.getOrDefault("contains_hldy", false);

						LocalDateTime prdBgngYmd = SoltechStreamUtils.parseDateTime(strPrdBgngYmd).toLocalDate()
								.atStartOfDay();
						LocalDateTime prdEndYmd = SoltechStreamUtils.parseDateTime(strPrdEndYmd).toLocalDate()
								.atStartOfDay();

						Set<LocalDate> holidaysSet = new HashSet<LocalDate>();
						List<HldyModel> hldyModelList = hldyService.inqHldy(new HldyParamDTO(), null).getContent();
						hldyModelList.forEach(hldyModel -> {
							holidaysSet.add(SoltechStreamUtils.parseDateTime(hldyModel.getHldyYmd()).toLocalDate());
						});
						List<LocalDateTime> localDateTimeList = SoltechStreamUtils.calcDateBetween(prdBgngYmd,
								prdEndYmd, containsHldy, holidaysSet);
						double prdDay = (double) localDateTimeList.size();

						/*** (CLSF) 휴가구분코드 */
						String vctnSeCd = (String) mapping.get("cmmn_cd_id_1020");
						if (!ObjectUtils.isEmpty(vctnSeCd)) {
							List<VctnModel> vctnModelList = new ArrayList<VctnModel>();
							if ((int) prdDay > 1) {
								LocalDateTime startLocalDateTime = null;
								LocalDateTime preLocalDateTime = null;
								for (int idx = 0; idx < localDateTimeList.size(); idx++) {
									LocalDateTime localDateTime = localDateTimeList.get(idx);
									if (ObjectUtils.isEmpty(startLocalDateTime)) {
										startLocalDateTime = localDateTime;
									}
									if (!ObjectUtils.isEmpty(preLocalDateTime)
											&& Duration.between(preLocalDateTime, localDateTime).toDays() > 1) {
										BigDecimal vctnUseCnts = BigDecimal
												.valueOf(-1 * SoltechStreamUtils.calcDateDiff(preLocalDateTime,
														localDateTime, containsHldy, holidaysSet));
										if (startLocalDateTime != null && !ObjectUtils.isEmpty(startLocalDateTime)) {
											strPrdBgngYmd = startLocalDateTime
													.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
										}
										if (preLocalDateTime != null && !ObjectUtils.isEmpty(preLocalDateTime)) {
											strPrdEndYmd = preLocalDateTime
													.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
										}
										vctnModelList.add(createVctnModel(atrzModel.getDrftrId(), vctnSeCd,
												strPrdBgngYmd, strPrdEndYmd, vctnUseCnts, atrzModel.getDocNo()));
										startLocalDateTime = null;
									}
									preLocalDateTime = localDateTime;
									if (idx == localDateTimeList.size() - 1) {
										BigDecimal vctnUseCnts = BigDecimal
												.valueOf(-1 * SoltechStreamUtils.calcDateDiff(preLocalDateTime,
														localDateTime, containsHldy, holidaysSet));
										if (startLocalDateTime != null && !ObjectUtils.isEmpty(startLocalDateTime)) {
											strPrdBgngYmd = startLocalDateTime
													.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
										}
										if (preLocalDateTime != null && !ObjectUtils.isEmpty(preLocalDateTime)) {
											strPrdEndYmd = preLocalDateTime
													.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
										}
										vctnModelList.add(createVctnModel(atrzModel.getDrftrId(), vctnSeCd,
												strPrdBgngYmd, strPrdEndYmd, vctnUseCnts, atrzModel.getDocNo()));
										startLocalDateTime = null;
									}
								}
							} else {
								BigDecimal vctnUseCnts = BigDecimal.valueOf(-1 * prdDay);
								if (vctnSeCd.startsWith("0002")) {
									/*** (CD) 반차 */
									vctnUseCnts = BigDecimal.valueOf(-1 * 0.5d);
								}
								vctnModelList.add(createVctnModel(atrzModel.getDrftrId(), vctnSeCd, strPrdBgngYmd,
										strPrdEndYmd, vctnUseCnts, atrzModel.getDocNo()));
							}
							vctnService.upsertAllVctn(vctnModelList);
						}
					}
				}
			}
		}

		return updatedRows;
	}

	/***
	 * 결재 삭제
	 */
	@CacheEvict(value = { "atrzCnt", "atrzCmptnCnt", "atrzUnCmptnCnt" }, allEntries = true)
	@Transactional
	@Override
	public int deleteAtrz(AtrzModel atrzModel) throws Exception {
		return atrzRepository.deleteAtrz(atrzModel);
	}

	/***
	 * 신규 등록시 다음으로 등록 될 문서 번호 조회
	 * 
	 * @param year : prefix의 연도
	 * @return 문서 번호
	 * @throws Exception
	 */
	private String getMaxDocNo(String year) throws Exception {
		if (ObjectUtils.isEmpty(year)) {
			year = String.valueOf(LocalDateTime.now().getYear());
		}

		return atrzRepository.getMaxDocNo(year);
	}

	/***
	 * 결재 완료 목록 조회
	 */
	@Override
	public Page<AtrzModel> inqAtrzCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		return atrzRepository.inqAtrzCmptn(atrzParamDTO, pageable);
	}

	/***
	 * 결재 미완료 목록 조회
	 */
	@Override
	public Page<AtrzModel> inqAtrzUnCmptn(AtrzParamDTO atrzParamDTO, Pageable pageable) throws Exception {
		return atrzRepository.inqAtrzUnCmptn(atrzParamDTO, pageable);
	}

	/***
	 * 결재 상태 수정
	 */
	@SuppressWarnings("unchecked")
	@CacheEvict(value = { "atrzCnt", "atrzCmptnCnt", "atrzUnCmptnCnt" }, allEntries = true)
	@Transactional
	@Override
	public int updateAtrzStts(AtrzModel atrzModel) throws Exception {
		if (ObjectUtils.isEmpty(atrzModel.getDocNo()) || ObjectUtils.isEmpty(atrzModel.getAprvrId())
				|| ObjectUtils.isEmpty(atrzModel.getAtrzDt()) || ObjectUtils.isEmpty(atrzModel.getAtrzSttsSeCd())) {
			throw CustomExceptionDTO.of("ERROR_0001", HttpStatus.BAD_REQUEST.value(),
					new BadRequestException("Validation Error"));
		}
		String updateKey = "atrz_info%";
		if (ObjectUtils.isEmpty(updateKey) || (!updateKey.startsWith("atrz_info") && !updateKey.startsWith("cprt_info")
				&& !updateKey.startsWith("rfrnc_info"))) {
			updateKey = "atrz_info%";
		} else if (!updateKey.endsWith("%")) {
			updateKey += "%";
		}
		int updatedRows = atrzRepository.updateAtrzStts(atrzModel, updateKey);

		if (updatedRows != 0 && !ObjectUtils.isEmpty(atrzModel.getDocSeCd())) {
			AtrzParamDTO atrzParamDTO = new AtrzParamDTO();
			atrzParamDTO.setDoc_no(atrzModel.getDocNo());
			AtrzModel changedAtrzModel = atrzRepository.getAtrz(atrzParamDTO);

			if ("00020".equals(changedAtrzModel.getAtrzSttsSeCd())) {
				/*** (CD) 결재완료 */
				Map<String, Object> atrzData = changedAtrzModel.getAtrzData();
				if (!ObjectUtils.isEmpty(atrzData.get("prd_info"))) {
					if (atrzData.get("prd_info") instanceof Map) {
						Map<String, Object> prdInfo = (Map<String, Object>) atrzData.get("prd_info");
						CmmnCdParamDTO cmmnCdParamDTO = new CmmnCdParamDTO();
						cmmnCdParamDTO.setCmmn_cd_id(changedAtrzModel.getDocSeCd());
						if (!ObjectUtils.isEmpty(prdInfo.get("vctn_cd"))) {
							cmmnCdParamDTO.setCmmn_cd_user_dfn_vl2((String) prdInfo.get("vctn_cd"));
						}
						Map<String, Object> mapping = cmmnCdService.getAtrzMapping(cmmnCdParamDTO);

						if (!ObjectUtils.isEmpty(mapping)) {
							/*** (CLSF) 휴가구분코드 */
							String vctnSeCd = (String) mapping.get("cmmn_cd_id_1020");

							String strPrdBgngYmd = (String) prdInfo.getOrDefault("prd_bgng_ymd", "");
							String strPrdEndYmd = (String) prdInfo.getOrDefault("prd_end_ymd", "");
							boolean containsHldy = (boolean) prdInfo.getOrDefault("contains_hldy", false);

							LocalDateTime prdBgngYmd = SoltechStreamUtils.parseDateTime(strPrdBgngYmd).toLocalDate()
									.atStartOfDay();
							LocalDateTime prdEndYmd = SoltechStreamUtils.parseDateTime(strPrdEndYmd).toLocalDate()
									.atStartOfDay();

							Set<LocalDate> holidaysSet = new HashSet<LocalDate>();
							List<HldyModel> hldyModelList = hldyService.inqHldy(new HldyParamDTO(), null).getContent();
							hldyModelList.forEach(hldyModel -> {
								holidaysSet.add(SoltechStreamUtils.parseDateTime(hldyModel.getHldyYmd()).toLocalDate());
							});
							List<LocalDateTime> localDateTimeList = SoltechStreamUtils.calcDateBetween(prdBgngYmd,
									prdEndYmd, containsHldy, holidaysSet);
							double prdDay = (double) localDateTimeList.size();

							/*** (CLSF) 근태구분코드 */
							String atdcSeCd = (String) mapping.get("cmmn_cd_id_1010");
							/*** (CLSF) 일정구분코드 */
							String schdlSeCd = (String) mapping.get("cmmn_cd_id_1030");
							String schdlSeNm = (String) mapping.get("cmmn_cd_nm_1030");

							List<AtdcModel> atdcModelList = new ArrayList<AtdcModel>();
							List<SchdlModel> schdlModelList = new ArrayList<SchdlModel>();
							if ((int) prdDay > 1) {
								LocalDateTime startLocalDateTime = null;
								LocalDateTime preLocalDateTime = null;
								for (int idx = 0; idx < localDateTimeList.size(); idx++) {
									LocalDateTime localDateTime = localDateTimeList.get(idx);
									if (ObjectUtils.isEmpty(startLocalDateTime)) {
										startLocalDateTime = localDateTime;
									}

									if (!ObjectUtils.isEmpty(atdcSeCd)) {
										String atdcTm = "0900";
										atdcModelList.add(createAtdcModel(changedAtrzModel.getDrftrId(),
												localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")), atdcTm,
												atdcSeCd));
									}

									if (!ObjectUtils.isEmpty(preLocalDateTime)
											&& Duration.between(preLocalDateTime, localDateTime).toDays() > 1) {
										if (!ObjectUtils.isEmpty(schdlSeCd)) {
											schdlModelList.add(createSchdlModel(SoltechStreamUtils.createEncodeId(),
													changedAtrzModel.getDrftrId(), schdlSeCd, "[" + schdlSeNm + "]",
													schdlSeNm, startLocalDateTime, preLocalDateTime));
										}
										startLocalDateTime = null;
									}
									preLocalDateTime = localDateTime;
									if (idx == localDateTimeList.size() - 1) {
										if (!ObjectUtils.isEmpty(schdlSeCd)) {
											schdlModelList.add(createSchdlModel(SoltechStreamUtils.createEncodeId(),
													changedAtrzModel.getDrftrId(), schdlSeCd, "[" + schdlSeNm + "]",
													schdlSeNm, startLocalDateTime, preLocalDateTime));
										}
										startLocalDateTime = null;
									}
								}
							} else {
								if (!ObjectUtils.isEmpty(atdcSeCd)) {
									String atdcTm = "0900";
									if ("00022".equals(vctnSeCd)) {
										/*** (CD) 오후 반차 */
										atdcTm = "1400";
									}
									atdcModelList.add(createAtdcModel(changedAtrzModel.getDrftrId(),
											prdBgngYmd.format(DateTimeFormatter.ofPattern("yyyyMMdd")), atdcTm,
											atdcSeCd));
								}
								if (!ObjectUtils.isEmpty(schdlSeCd)) {
									if ("00021".equals(vctnSeCd)) {
										/*** (CD) 오전 반차 */
										prdBgngYmd = prdBgngYmd.withHour(9);
										prdEndYmd = prdEndYmd.withHour(14);
									} else if ("00022".equals(vctnSeCd)) {
										/*** (CD) 오후 반차 */
										prdBgngYmd = prdBgngYmd.withHour(14);
										prdEndYmd = prdEndYmd.withHour(18);
									}
									schdlModelList.add(createSchdlModel(SoltechStreamUtils.createEncodeId(),
											changedAtrzModel.getDrftrId(), schdlSeCd, "[" + schdlSeNm + "]", schdlSeNm,
											prdBgngYmd, prdEndYmd));
								}
							}
							atdcService.upsertAllAtdc(atdcModelList);
							schdlService.upsertAllSchdl(schdlModelList);
						}
					}
				}
			}
		}

		return updatedRows;
	}

	/***
	 * 휴가 모델 클래스 생성
	 * 
	 * @param vctnUserId  : 휴가 사용자 ID
	 * @param vctnSeCd    : 휴가 구분 코드
	 * @param vctnBgngYmd : 휴가 시작 일자
	 * @param vctnEndYmd  : 휴가 종료 일자
	 * @param vctnUseCnts : 휴가 사용 수
	 * @param atrzDocNo   : 결재 문서 번호
	 * @return 생성된 휴가 모델 클래스
	 */
	private VctnModel createVctnModel(String vctnUserId, String vctnSeCd, String vctnBgngYmd, String vctnEndYmd,
			BigDecimal vctnUseCnts, String atrzDocNo) {
		VctnModel vctnModel = VctnModel.builder().vctnUserId(vctnUserId).vctnSeCd(vctnSeCd).vctnBgngYmd(vctnBgngYmd)
				.vctnEndYmd(vctnEndYmd).vctnUseCnt(vctnUseCnts).atrzDocNo(atrzDocNo).build();
		vctnModel.deserialize();
		vctnModel.preModel();
		vctnModel.prePersist();
		vctnModel = vctnModel.toBuilder().rgtrId("System").mdfrId("System").build();

		return vctnModel;
	}

	/***
	 * 근태 모델 클래스 생성
	 * 
	 * @param atdcUserId : 근태 사용자 ID
	 * @param atdcYmd    : 근태 일자
	 * @param atdcTm     : 근태 시각
	 * @param atdcSeCd   : 근태 구분 코드
	 * @return 생성된 근태 모델 클래스
	 */
	private AtdcModel createAtdcModel(String atdcUserId, String atdcYmd, String atdcTm, String atdcSeCd) {
		AtdcModel atdcModel = AtdcModel.builder().atdcUserId(atdcUserId).atdcYmd(atdcYmd).atdcTm(atdcTm)
				.atdcSeCd(atdcSeCd).build();
		atdcModel.deserialize();
		atdcModel.preModel();
		atdcModel.prePersist();
		atdcModel = atdcModel.toBuilder().rgtrId("System").mdfrId("System").build();

		return atdcModel;
	}

	/***
	 * 일정 모델 클래스 생성
	 * 
	 * @param schdlId     : 일정 ID
	 * @param schdlUserId : 일정 사용자 ID
	 * @param schdlSeCd   : 일정 구분 코드
	 * @param schdlTtl    : 일정 제목
	 * @param schdlCn     : 일정 내용
	 * @param schdlBgngDt : 일정 시작 일시
	 * @param schdlEndDt  : 일정 종료 일시
	 * @return 생성된 일정 모델 클래스
	 */
	private SchdlModel createSchdlModel(String schdlId, String schdlUserId, String schdlSeCd, String schdlTtl,
			String schdlCn, LocalDateTime schdlBgngDt, LocalDateTime schdlEndDt) {
		SchdlModel schdlModel = SchdlModel.builder().schdlId(schdlId).schdlUserId(schdlUserId).schdlSeCd(schdlSeCd)
				.schdlTtl(schdlTtl).schdlCn(schdlCn).schdlBgngDt(schdlBgngDt).schdlEndDt(schdlEndDt).build();
		schdlModel.deserialize();
		schdlModel.prePersist();
		schdlModel = schdlModel.toBuilder().rgtrId("System").mdfrId("System").build();

		return schdlModel;
	}
}