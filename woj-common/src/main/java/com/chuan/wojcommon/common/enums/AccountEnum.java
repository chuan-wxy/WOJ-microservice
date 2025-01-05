package com.chuan.wojcommon.common.enums;

/**
 * 账户枚举类
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:15
 * @Description:
 */
public enum AccountEnum {
    CODE_CHANGE_PASSWORD_FAIL("change-password-fail:"),
    CODE_CHANGE_PASSWORD_LOCK("change-password-lock:"),
    CODE_ACCOUNT_LOCK("account-lock:"),
    CODE_CHANGE_EMAIL_FAIL("change-email-fail:"),
    CODE_CHANGE_EMAIL_LOCK("change-email-lock:"),

    /**
     * 用户登录次数前缀
     */
    TRY_LOGIN_NUM("try-login-num:"),
    /**
     * 用户注销次数前缀
     */
    TRY_Logout_NUM("try-login-num:"),

    ACM_RANK_CACHE("acm_rank_cache"),
    OI_RANK_CACHE("oi_rank_cache"),

    GROUP_RANK_CACHE("group_rank_cache"),

    SUPER_ADMIN_UID_LIST_CACHE("super_admin_uid_list_case"),

    SUBMIT_NON_CONTEST_LOCK("submit_non_contest_lock:"),
    TEST_JUDGE_LOCK("test_judge_lock:"),
    SUBMIT_CONTEST_LOCK("submit_contest_lock:"),
    DISCUSSION_ADD_NUM_LOCK("discussion_add_num_lock:"),
    GROUP_ADD_NUM_LOCK("group_add_num_lock"),
    CONTEST_ADD_PRINT_LOCK("contest_add_print_lock:"),

    REMOTE_JUDGE_CF_ACCOUNT_NUM("remote_judge_cf_account:");

    private final String code;

    AccountEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
