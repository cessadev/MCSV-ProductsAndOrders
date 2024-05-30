package com.cessadev.mcsv_notification.events;

import com.cessadev.mcsv_notification.model.enums.EOrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, EOrderStatus orderStatus) { }
