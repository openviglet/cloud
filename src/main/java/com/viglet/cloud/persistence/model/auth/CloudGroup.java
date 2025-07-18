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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * The persistent class for the TurGroup database table.
 * 
 */

@Getter
@Entity
@Table(name = "auth_group")
public class CloudGroup implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Setter
	@Id
	@UuidGenerator
	@Column(updatable = false, nullable = false)
	private String id;

	@Setter
	private String name;

	@Setter
	private String description;

	@ManyToMany
	private Collection<CloudRole> roles = new HashSet<>();

	@ManyToMany(mappedBy = "groups")
	private Collection<CloudUser> users = new HashSet<>();

	public void setCloudUsers(Collection<CloudUser> users) {
		this.users.clear();
		if (users != null) {
			this.users.addAll(users);
		}
	}
	public void setCloudRoles(Collection<CloudRole> roles) {
		this.roles.clear();
		if (roles != null) {
			this.roles.addAll(roles);
		}
	}
}
