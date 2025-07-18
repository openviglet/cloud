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

package com.viglet.cloud.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.viglet.cloud.persistence.model.auth.CloudGroup;
import com.viglet.cloud.persistence.model.auth.CloudRole;
import com.viglet.cloud.persistence.model.auth.CloudUser;
import com.viglet.cloud.persistence.repository.auth.CloudGroupRepository;
import com.viglet.cloud.persistence.repository.auth.CloudRoleRepository;
import com.viglet.cloud.persistence.repository.auth.CloudUserRepository;

@Service("customUserDetailsService")
public class CloudCustomUserDetailsService implements UserDetailsService {

    private final CloudUserRepository cloudUserRepository;
    private final CloudRoleRepository cloudRoleRepository;
    private final CloudGroupRepository cloudGroupRepository;

    public CloudCustomUserDetailsService(CloudUserRepository cloudUserRepository,
            CloudRoleRepository cloudRoleRepository,
            CloudGroupRepository cloudGroupRepository) {
        this.cloudUserRepository = cloudUserRepository;
        this.cloudRoleRepository = cloudRoleRepository;
        this.cloudGroupRepository = cloudGroupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CloudUser turUser = cloudUserRepository.findByUsername(username);
        if (null == turUser) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            List<CloudUser> users = new ArrayList<>();
            users.add(turUser);
            Set<CloudGroup> cloudGroups = cloudGroupRepository.findByUsersIn(users);
            Set<CloudRole> cloudRoles = cloudRoleRepository.findByGroupsIn(cloudGroups);

            List<String> roles = new ArrayList<>();
            for (CloudRole role : cloudRoles) {
                roles.add(role.getName());
            }
            return new CloudCustomUserDetails(turUser, roles);
        }
    }
}
