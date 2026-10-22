package com.hope.xueling.common.service;

import com.hope.xueling.common.domain.entity.User;
import com.hope.xueling.common.mapper.UserMapper;
import com.hope.xueling.common.service.impl.UserServiceImpl;
import com.hope.xueling.common.util.AliOSSUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserVipStatusTest {
    @Test
    void expiredMembershipIsNotShownAsActive() {
        UserMapper mapper = mock(UserMapper.class);
        User user = new User();
        user.setId(42L);
        user.setVipLevel(2);
        when(mapper.selectById(42L)).thenReturn(user);
        when(mapper.isVip(42L)).thenReturn(null);
        UserServiceImpl service = new UserServiceImpl(mapper, mock(AliOSSUtils.class));

        assertEquals(0, service.getUserInfo(42L).getVipLevel());
        when(mapper.isVip(42L)).thenReturn(42L);
        assertEquals(2, service.getUserInfo(42L).getVipLevel());
    }
}
