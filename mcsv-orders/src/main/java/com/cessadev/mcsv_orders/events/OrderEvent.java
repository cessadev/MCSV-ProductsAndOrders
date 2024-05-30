package com.cessadev.mcsv_orders.events;

import com.cessadev.mcsv_orders.model.enums.EOrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, EOrderStatus orderStatus) { }
