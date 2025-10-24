package dev.mudris.mapper;

import org.springframework.stereotype.Component;

import dev.mudris.dto.TagDto;
import dev.mudris.entity.Tag;

@Component
public class TagMapper {

	public TagDto toTagDto(Tag tag) {
		return new TagDto(tag.getId(), tag.getName());
	}
}
