package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.UnitCreateRequest;
import com.project.pharmacy.dto.request.UnitUpdateRequest;
import com.project.pharmacy.dto.response.UnitResponse;
import com.project.pharmacy.entity.Unit;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.UnitMapper;
import com.project.pharmacy.repository.PriceRepository;
import com.project.pharmacy.repository.UnitRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitService {
    UnitRepository unitRepository;

    PriceRepository priceRepository;

    UnitMapper unitMapper;

    //Them Don Vi
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponse createUnit(UnitCreateRequest request){
        if(unitRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.UNIT_EXISTED);

        Unit unit = unitMapper.toUnit(request);
        unitRepository.save(unit);

        return unitMapper.toUnitResponse(unit);
    }

    //Xem danh sach don vi
    @PreAuthorize("hasRole('ADMIN')")
    public List<UnitResponse> getUnit(){
        return unitRepository.findAll().stream()
                .map(unitMapper::toUnitResponse)
                .collect(Collectors.toList());
    }

    //Sua don vi
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponse updateUnit(UnitUpdateRequest request){
        Unit unit = unitRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        unitMapper.updateUnit(unit, request);
        unitRepository.save(unit);

        return unitMapper.toUnitResponse(unit);
    }

    //Xoa don vi
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUnit(String id){
        Unit unit = unitRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));
        priceRepository.deleteAllByUnitId(id);
        unitRepository.deleteById(unit.getId());
    }
}
