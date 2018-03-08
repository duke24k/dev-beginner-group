package com.jojoldu.admin.service;

import com.jojoldu.admin.config.WebProperties;
import com.jojoldu.admin.dto.LetterAdminSaveRequestDto;
import com.jojoldu.admin.dto.LetterPageRequestDto;
import com.jojoldu.admin.dto.letter.save.LetterContentResponseDto;
import com.jojoldu.admin.dto.mail.ArchiveDto;
import com.jojoldu.admin.dto.mail.MailContentDto;
import com.jojoldu.admin.dto.mail.MailSendDto;
import com.jojoldu.admin.repository.lettercontent.LetterContentWebRepository;
import com.jojoldu.beginner.domain.letter.Letter;
import com.jojoldu.beginner.domain.letter.LetterContent;
import com.jojoldu.beginner.domain.letter.LetterRepository;
import com.jojoldu.beginner.domain.subscriber.Subscriber;
import com.jojoldu.beginner.domain.subscriber.SubscriberRepository;
import com.jojoldu.beginner.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jojoldu@gmail.com on 2017. 11. 24.
 * Blog : http://jojoldu.tistory.com
 * Github : https://github.com/jojoldu
 */

@Slf4j
@Service
@AllArgsConstructor
public class LetterAdminService {

    private LetterRepository letterRepository;
    private LetterContentWebRepository letterContentRepository;
    private SubscriberRepository subscriberRepository;
    private WebProperties webProperties;
    private ArchiveFactory archiveFactory;

    @Transactional(readOnly = true)
    public List<LetterContentResponseDto> findByPageable(LetterPageRequestDto dto){
        return letterContentRepository.findLetterContentDto(dto.toPageable());
    }

    @Transactional(readOnly = true)
    public List<MailSendDto> createSendMailList(Long letterId, List<String> emails){
        Letter letter = findLetter(letterId);
        return subscriberRepository.findAllActive(emails).stream()
                .map(subscriber -> MailSendDto.builder()
                        .letterId(letter.getId())
                        .subject(letter.getSubject())
                        .email(subscriber.getEmail())
                        .mailContents(createMailContents(subscriber.getId(), letter.getContentEntities()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long saveLetter(LetterAdminSaveRequestDto dto) {
        Letter letter = dto.toEntity();
        List<LetterContent> letterContents = letterContentRepository.findAllByIdIn(dto.getContentIds());
        letter.addContents(letterContents);
        letter.updateArchive(archiveFactory.createArchive(createArchiveDto(dto.getSubject(), letterContents)));
        return letterRepository.save(letter).getId();
    }

    private Letter findLetter(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당하는 Letter가 없습니다. ID: %d", letterId)));
    }

    private ArchiveDto createArchiveDto(String subject, List<LetterContent> letterContents){
        return new ArchiveDto(subject, createMailContents(findAdminId(), letterContents));
    }

    private Long findAdminId(){
        return subscriberRepository.findTopByEmail(Constants.ADMIN_EMAIL)
                .map(Subscriber::getId)
                .orElse(1L);
    }

    private List<MailContentDto> createMailContents(Long subscriberId, List<LetterContent> contents) {
        return contents.stream()
                .map(content -> new MailContentDto(subscriberId, webProperties.getWebUrl(), content))
                .collect(Collectors.toList());
    }
}
