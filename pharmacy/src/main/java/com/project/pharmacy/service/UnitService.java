package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.UnitCreateRequest;
import com.project.pharmacy.dto.request.UnitUpdateRequest;
import com.project.pharmacy.dto.response.UnitResponse;
import com.project.pharmacy.entity.Category;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.Unit;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ImageRepository;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.repository.UnitRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitService {
    UnitRepository unitRepository;

    ProductRepository productRepository;

    ImageRepository imageRepository;
    //Them Don Vi
    public UnitResponse createUnit(UnitCreateRequest request){
        if(unitRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.UNIT_EXISTED);

        Unit unit = new Unit();
        unit.setName(request.getName());
        unit.setDescription(request.getDescription());
        unitRepository.save(unit);

        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .build();
    }

    //Xem danh sach don vi
    public List<UnitResponse> getUnit(){
        List<Unit> units = unitRepository.findAll();
        List<UnitResponse> unitResponses = new ArrayList<>();

        for(Unit unit : units){
            UnitResponse temp = UnitResponse.builder()
                    .id(unit.getId())
                    .name(unit.getName())
                    .description(unit.getDescription())
                    .build();
            unitResponses.add(temp);
        }
        return unitResponses;
    }

    //Sua don vi
    public UnitResponse updateUnit(UnitUpdateRequest request, String id){
        Unit unit = unitRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        unit.setName(request.getName());
        unit.setDescription(request.getDescription());

        unitRepository.save(unit);

        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .build();
    }

    //Xoa don vi
    @Transactional
    public void deleteUnit(String id){
        Unit unit = unitRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        productRepository.updateUnitIdToNull(id);

        unitRepository.deleteById(id);
    }
}
