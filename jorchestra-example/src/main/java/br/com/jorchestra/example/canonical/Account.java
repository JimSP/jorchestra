package br.com.jorchestra.example.canonical;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Account implements Serializable {

	private static final long serialVersionUID = 6858788904693835444L;

	public static AccountBuilder create() {
		return AccountBuilder.create(); 
	}
	
	private final Long accountNumber;

	@JsonCreator
	public Account(@JsonProperty("accountNumber") final Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + "]";
	}
}
