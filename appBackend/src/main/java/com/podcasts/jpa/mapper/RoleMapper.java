package com.podcasts.jpa.mapper;

import com.podcasts.jpa.pojo.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleMapper extends JpaRepository<Role, Long> {
}
