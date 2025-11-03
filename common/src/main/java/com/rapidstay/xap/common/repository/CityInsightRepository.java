package com.rapidstay.xap.common.repository;

import com.rapidstay.xap.common.entity.CityInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityInsightRepository extends JpaRepository<CityInsight, Long> {

    Optional<CityInsight> findByCityNameIgnoreCase(String cityName);

}
