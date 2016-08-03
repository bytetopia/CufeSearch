package com.cufesearch.my;

/**
 * JavaBean类，用于web显示搜索结果文档
 *
 */
public class ResultDoc {
	private String docTitle;
	private String docAbstract;
	private String docKeywords;
	private float docScore;
	private String docUrl;
	private String docDate;
	public String getDocDate() {
		return docDate;
	}
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public String getDocAbstract() {
		return docAbstract;
	}
	public void setDocAbstract(String docAbstract) {
		this.docAbstract = docAbstract;
	}
	public String getDocKeywords() {
		return docKeywords;
	}
	public void setDocKeywords(String docKeywords) {
		this.docKeywords = docKeywords;
	}
	public float getDocScore() {
		return docScore;
	}
	public void setDocScore(float docScore) {
		this.docScore = docScore;
	}
	public String getDocUrl() {
		return docUrl;
	}
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	@Override
	public String toString() {
		return "ResultDoc [docTitle=" + docTitle + ", docAbstract=" + docAbstract + ", docKeywords=" + docKeywords
				+ ", docScore=" + docScore + ", docUrl=" + docUrl + ", docDate=" + docDate + "]";
	}
	
	
	
}
