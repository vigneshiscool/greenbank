package entity;
// default package
// Generated Oct 21, 2013 10:26:11 PM by Hibernate Tools 3.4.0.CR1

/**
 * AuthorizationsUserEmployee generated by hbm2java
 */
public class AuthorizationsUserEmployee {

	private int authorizationId;
	private String externalUserId;
	private String internalUserId;

	public AuthorizationsUserEmployee() {
	}

	public AuthorizationsUserEmployee(int authorizationId,
			String externalUserId, String internalUserId) {
		this.authorizationId = authorizationId;
		this.externalUserId = externalUserId;
		this.internalUserId = internalUserId;
	}

	public int getAuthorizationId() {
		return this.authorizationId;
	}

	public void setAuthorizationId(int authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getExternalUserId() {
		return this.externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getInternalUserId() {
		return this.internalUserId;
	}

	public void setInternalUserId(String internalUserId) {
		this.internalUserId = internalUserId;
	}

}
