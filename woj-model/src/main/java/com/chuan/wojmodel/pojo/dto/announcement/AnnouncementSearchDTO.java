package com.chuan.wojmodel.pojo.dto.announcement;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author chuan_wxy
 *
 */
@Data
public class AnnouncementSearchDTO implements Serializable {

    private Long id;

    private String title;

    @Serial
    private static final long serialVersionUID = 1L;
}
