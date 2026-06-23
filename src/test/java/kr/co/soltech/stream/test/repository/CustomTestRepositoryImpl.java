package kr.co.soltech.stream.test.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kr.co.soltech.stream.msg.model.MsgModel;

public class CustomTestRepositoryImpl implements CustomTestRepository {
	/***
	 * JPA 엔티티 매니저
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/***
	 * 메세지 언어로 메세지 목록 조회(Criteria 방식)
	 */
	@Override
	public List<MsgModel> inqMsgByMsgLangManagerCriteria(String msgLang) throws Exception {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgModel> criteriaQuery = criteriaBuilder.createQuery(MsgModel.class);
		Root<MsgModel> root = criteriaQuery.from(MsgModel.class);

		/*
		 * List<Predicate> predicateList = new ArrayList<>();
		 * predicateList.add(criteriaBuilder.equal(root.get("useYn"), "Y")); if
		 * (!ObjectUtils.isEmpty("")) {
		 * predicateList.add(criteriaBuilder.equal(root.get(""), "")); } if
		 * (!ObjectUtils.isEmpty("") && !ObjectUtils.isEmpty("")) {
		 * predicateList.add(criteriaBuilder.between(root.get(""), "", "")); } if
		 * (!ObjectUtils.isEmpty("")) {
		 * predicateList.add(criteriaBuilder.like(root.get(""), "%" + "" + "%")); } if
		 * (!predicateList.isEmpty()) {
		 * criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new
		 * Predicate[0]))); }
		 */

		Predicate useYnPredicate = criteriaBuilder.equal(root.get("useYn"), "Y");
		Predicate msgCnPredicate = criteriaBuilder.equal(root.get("msgLang"), msgLang);
		criteriaQuery.select(root).where(criteriaBuilder.and(useYnPredicate, msgCnPredicate))
				.orderBy(criteriaBuilder.asc(root.get("msgClsf")));

		TypedQuery<MsgModel> typedQuery = entityManager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	/***
	 * 메세지 언어로 메세지 목록 조회(매니저 JPQL 방식)
	 */
	@Override
	public List<MsgModel> inqMsgByMsgLangManagerJPQL(String msgLang) throws Exception {
		String queryStr = """
				SELECT
					msg
				FROM
					msg_table msg
				WHERE
					msg.useYn = 'Y'
					AND msg.msgLang = :msgLang
				ORDER BY
					msg.msgLang,
					msg.msgClsf,
					msg.msgCd
				""";

		return entityManager.createQuery(queryStr.stripIndent().replace("\t", " ").replace("\n", " "), MsgModel.class)
				.setParameter("msgLang", msgLang).getResultList();
	}
}