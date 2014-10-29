/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.notes;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.notes.TagResourceAssembler.TagResource;

@RestController
@RequestMapping("tags")
public class TagsController {

	private final TagRepository repository;

	private final TagResourceAssembler resourceAssembler;

	@Autowired
	public TagsController(TagRepository repository, TagResourceAssembler resourceAssembler) {
		this.repository = repository;
		this.resourceAssembler = resourceAssembler;
	}

	@RequestMapping(method = RequestMethod.GET)
	Iterable<TagResource> all() {
		return this.resourceAssembler.toResources(this.repository.findAll());
	}

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	HttpHeaders create(@RequestBody TagInput tagInput) {
		Tag tag = new Tag();
		tag.setName(tagInput.getName());

		this.repository.save(tag);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(TagsController.class).slash(tag.getId()).toUri());

		return httpHeaders;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Resource<Tag> tag(@PathVariable("id") long id) {
		Tag tag = this.repository.findOne(id);
		return this.resourceAssembler.toResource(tag);
	}

	@RequestMapping(value = "/{id}/notes", method = RequestMethod.GET)
	ResourceSupport tagNotes(@PathVariable("id") long id) {
		ResourceSupport resource = new ResourceSupport();
		Tag tag = this.repository.findOne(id);
		for (Note note : tag.getNotes()) {
			resource.add(linkTo(NotesController.class).slash(note.getId())
					.withRel("note"));
		}
		return resource;
	}
}
