package co.miracleLab.jira.model;

public class ItemException extends Exception {
	private String ErrorTitle;
	private String ErrorBody;
	public ItemException () {
		
	}
	public ItemException (String errorTitle, String errorBody) {
		this.setErrorBody(errorBody);
		this.setErrorTitle(errorTitle);		
	}
	public String getErrorBody() {
		return ErrorBody;
	}
	public void setErrorBody(String errorBody) {
		ErrorBody = errorBody;
	}
	public String getErrorTitle() {
		return ErrorTitle;
	}
	public void setErrorTitle(String errorTitle) {
		ErrorTitle = errorTitle;
	}
}
