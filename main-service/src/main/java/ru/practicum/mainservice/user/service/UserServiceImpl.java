package ru.practicum.mainservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> usersPage;

        if (ids == null || ids.isEmpty()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            usersPage = userRepository.findByIdIn(ids, pageable);
        }

        return usersPage.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found.", userId));
        }

        userRepository.deleteById(userId);
    }
}
