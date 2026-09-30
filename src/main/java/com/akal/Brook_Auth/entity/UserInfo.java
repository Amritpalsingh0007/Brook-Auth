package com.akal.Brook_Auth.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user_info")
public class UserInfo{
	@Id
	@GeneratedValue
	@Column(name = "user_id")
	long id;
	@Column(nullable = false, unique = true)
	String username;
	@Column(nullable = false)
	String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<UserRoles> userRoles = new HashSet<>();

	public UserInfo(String username, String password){
		this.username = username;
		this.password = password;
	}
}
