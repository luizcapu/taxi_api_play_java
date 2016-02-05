package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;


@Entity
public class Driver extends Model{
	@Id
	public Integer id;
	@Column(nullable=false)
	public Double latitude;
	@Column(nullable=false)
	public Double longitude;
	@Column(nullable=false)
	public boolean available;
	
	public static Finder<Long, Driver> find = new Finder<Long,Driver>(Driver.class);
}
