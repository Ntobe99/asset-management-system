package com.company.assetmanagement.service;

import com.company.assetmanagement.dto.CategoryDto;
import com.company.assetmanagement.entity.Category;
import com.company.assetmanagement.exception.BadRequestException;
import com.company.assetmanagement.exception.ResourceNotFoundException;
import com.company.assetmanagement.repository.AssetRepository;
import com.company.assetmanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AssetRepository assetRepository;

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    public CategoryDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public CategoryDto create(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        return toDto(categoryRepository.save(category));
    }

    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = findEntity(id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return toDto(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = findEntity(id);
        categoryRepository.delete(category);
    }

    private Category findEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryDto toDto(Category category) {
        long count = assetRepository.count((root, query, cb) -> cb.equal(root.get("category").get("id"), category.getId()));
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .assetCount(count)
                .build();
    }
}
