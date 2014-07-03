package com.vf.reminder.db.tables;

import android.util.Log;

public class Friends {

	private String idMobile;
	private String group;
	private String stat;
	public String getIdMobile() {
		return idMobile;
	}
	public void setIdMobile(String idMobile) {
		this.idMobile = idMobile;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getStat(){
		return stat;
	}
	
	public void setStat(String stat){
		this.stat = stat;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idMobile == null) ? 0 : idMobile.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Log.d("TEMP","inside equals "+obj);
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass() )
			return false;
		Friends other = (Friends) obj;
		if (idMobile == null) {
			if (other.idMobile != null)
				return false;
		} else if (!idMobile.equals(other.idMobile))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Friends [idMobile=" + idMobile + ", stat=" + stat + "]";
	}
	
}
