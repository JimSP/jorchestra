package br.com.jorchestra.example.canonical;

public final class AccountBuilder {

	public static AccountBuilder create() {
		return new AccountBuilder();
	}

	private Long accountNumber;

	private AccountBuilder() {

	}

	public AccountBuilder withAccountNumber(final Long accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}

	public Account build() {
		return new Account(accountNumber);
	}
}
