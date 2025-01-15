package com.chuan.wojcommon.constant;

public class RedisContant {
    /**
     * 用户 token 前缀
     */
    public static final String USER_TOKEN = "user_token:";
    /**
     * 用户登录次数前缀
     */
    public static final String TRY_LOGIN_NUM = "try-login-num:";
    /**
     * 用户注销次数前缀
     */
    public static final String TRY_Logout_NUM = "try-login-num:";
    public static final String CODE_CHANGE_PASSWORD_FAIL = "change-password-fail:";
    public static final String CODE_ACCOUNT_LOCK = "account-lock:";
    public static final String CODE_CHANGE_EMAIL_FAIL = "change-email-fail:";
    public static final String CODE_CHANGE_EMAIL_LOCK = "change-email-lock:";
    public static final String ACM_RANK_CACHE = "acm_rank_cache";
    public static final String OI_RANK_CACHE = "oi_rank_cache";
    public static final String GROUP_RANK_CACHE = "group_rank_cache";
    public static final String SUPER_ADMIN_UID_LIST_CACHE = "super_admin_uid_list_case";
    public static final String SUBMIT_NON_CONTEST_LOCK = "submit_non_contest_lock:";
    public static final String TEST_JUDGE_LOCK = "test_judge_lock:";
    public static final String SUBMIT_CONTEST_LOCK = "submit_contest_lock:";
    public static final String DISCUSSION_ADD_NUM_LOCK = "discussion_add_num_lock:";
    public static final String GROUP_ADD_NUM_LOCK = "group_add_num_lock";
    public static final String CONTEST_ADD_PRINT_LOCK = "contest_add_print_lock:";
    public static final String REMOTE_JUDGE_CF_ACCOUNT_NUM = "remote_judge_cf_account:";
}
