package com.thentrees.shopapp.utils;

public class MessageKeys {

    // auth
    public static final String LOGIN_SUCCESSFULLY = "user.login.login_successfully";
    public static final String REGISTER_SUCCESSFULLY = "user.login.register_successfully";
    public static final String LOGIN_FAILED = "user.login.login_failed";
    public static final String PASSWORD_NOT_MATCH = "user.register.password_not_match";
    public static final String USER_IS_LOCKED = "user.login.user_is_locked";

    // category
    public static final String INSERT_CATEGORY_SUCCESSFULLY = "category.create_category.create_successfully";
    public static final String DELETE_CATEGORY_SUCCESSFULLY = "category.delete_category.delete_successfully";
    public static final String UPDATE_CATEGORY_SUCCESSFULLY = "category.update_category.update_successfully";

    // comment
    public static final String COMMENT_FAIL = "comment.insert.create_failed";
    public static final String INSERT_COMMENT_SUCCESSFULLY = "comment.insert.create_successfully";
    public static final String DELETE_COMMENT_SUCCESSFULLY = "comment.delete_comment.delete_successfully";
    public static final String UPDATE_COMMENT_SUCCESSFULLY = "comment.update_comment.update_successfully";
    public static final String DELETE_COMMENT_FAIL = "comment.delete_comment.delete_failed";

    // order
    public static final String DELETE_ORDER_SUCCESSFULLY = "order.delete_order.delete_successfully";
    public static final String DELETE_ORDER_DETAIL_SUCCESSFULLY = "order.delete_order_detail.delete_successfully";
    public static final String UPLOAD_IMAGES_MAX_5 = "product.upload_images.error_max_5_images";
    public static final String UPLOAD_IMAGES_FILE_LARGE = "product.upload_images.file_large";
    public static final String UPLOAD_IMAGES_FILE_MUST_BE_IMAGE = "product.upload_images.file_must_be_image";
    public static final String INSERT_CATEGORY_FAILED = "category.create_category.create_failed";
    public static final String WRONG_PHONE_PASSWORD = "user.login.wrong_phone_password";
    public static final String ROLE_DOES_NOT_EXISTS = "user.login.role_not_exist";
    public static final String PHONE_EXISTED = "user.register.phone_existed";
    public static final String NOT_COMMENT = "comment.update.not_update";

    //    utils
    public static final String NOT_FOUND = "not_found";
    public static final String CAN_NOT_DELETE = "can_not_delete";
    public static final String DATE_IS_BEFORE = "date_is_before";
    public static final String INVALID_QUANTITY = "invalid_quantity";
    public static final String NOT_ACTIVE = "not_active";

    //   coupon
    public static final String COUPON_ALREADY_EXISTS = "coupon.get.coupon_already_exists";

    // token
    public static final String TOKEN_EXPISTRING = "token.get.token_expiring";
    ///////////////////////////////
    //payment

    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_INCOMPLETE = "payment.incomplete";
    public static final String PAYMENT_DEFECTIVE = "payment.defective";
    public static final String PAYMENT_ISLAND = "payment.island";
    public static final String PAYMENT_PROCESSING= "payment.processing";
    public static final String PAYMENT_REQUEST_REFUND= "payment.request.refund";
    public static final String PAYMENT_SUSPECTED= "payment.suspected";
    public static final String PAYMENT_REFUND_DENIED= "payment.refund.denied";




}
