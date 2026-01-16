package com.estapar.teste.infrastructure.adapters.out.persistence.repository;

import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSectorRepository extends JpaRepository<SectorEntity, String> {
}
