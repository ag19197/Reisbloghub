package com.Reisblog.dto.user;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import lombok.Data;

//个人主页
@Data
public class UserProfileDTO {
    private Long userId;
    private String nickname;
    private String avatar;
    private PageResult<PublicCollectionDTO> publicCollections;
}