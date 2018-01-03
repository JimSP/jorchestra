package br.com.jorchestra.example.canonical;

public enum Status {
	SUCCESS, ERROR;

	public static Boolean isSuccess(final Status status) {
		return status == SUCCESS;
	}
}
