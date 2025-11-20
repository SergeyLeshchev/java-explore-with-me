package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.UpdateCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UniqueConstraintException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new UniqueConstraintException("Категория с именем " + newCategoryDto.getName() + " уже существует");
        }
        Category category = CategoryMapper.mapToCategory(newCategoryDto);
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не найдена"));
        if (eventRepository.existsByCategoryName(category.getName())) {
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto updateCategory(UpdateCategoryDto newCategoryDto, Long catId) {
        if (newCategoryDto == null || newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            throw new BadRequestException("Для обновления категории нужно передать новые данные");
        }
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не найдена"));
        if (category.getName().equals(newCategoryDto.getName())) {
            return CategoryMapper.mapToCategoryDto(category);
        }
        category.setName(newCategoryDto.getName());
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }
}
