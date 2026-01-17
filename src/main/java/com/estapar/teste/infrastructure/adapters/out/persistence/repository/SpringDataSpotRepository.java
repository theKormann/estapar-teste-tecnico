package com.estapar.teste.infrastructure.adapters.out.persistence.repository;

import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSpotRepository extends JpaRepository<SpotEntity, Long> {
}