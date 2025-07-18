/*
 * Copyright (C) 2016-2022 the original author or authors. 
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.viglet.cloud.persistence.model.auth;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * The persistent class for the TurUser database table.
 * 
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "auth_user")
public class CloudUser implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Setter
	@Id
	private String username;

	@Setter
	private String confirmEmail;

	@Setter
	private String email;

	@Setter
	private String firstName;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	@Setter
	private String lastName;

	@Setter
	private String lastPostType;

	@Setter
	private int loginTimes;

	@Setter
	private String password;

	@Setter
	private String realm;

	@Setter
	private String recoverPassword;

	@Setter
	private int enabled;

	@Builder.Default
	@ManyToMany
	private Collection<CloudGroup> groups = new HashSet<>();

	public CloudUser(com.viglet.cloud.persistence.model.auth.CloudUser user) {
		this.username = user.username;
		this.email = user.email;
		this.password = user.password;
		this.enabled = user.enabled;
	}

	public void setGroups(Collection<CloudGroup> groups) {
		this.groups.clear();
		if (groups != null) {
			this.groups.addAll(groups);
		}
	}
}
