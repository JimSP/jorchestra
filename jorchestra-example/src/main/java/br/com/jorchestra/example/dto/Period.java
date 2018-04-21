package br.com.jorchestra.example.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Period implements Serializable {

	private static final long serialVersionUID = -8602397072910835514L;

	private final Date from;
	private final Date to;

	public Period(@JsonProperty("from") final Date from, @JsonProperty("to") final Date to) {
		super();
		this.from = from;
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		Period other = (Period) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Period [from=" + from + ", to=" + to + "]";
	}
}
