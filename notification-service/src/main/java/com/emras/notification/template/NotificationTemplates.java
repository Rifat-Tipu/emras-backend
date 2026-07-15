package com.emras.notification.template;

import com.emras.notification.entity.Notification;

import java.math.BigDecimal;

/**
 * Simple string-based notification templates.
 *
 * In production, replace with Thymeleaf HTML email templates.
 * For now, plain text is logged to console in local dev.
 *
 * Supports both English and Bangla titles.
 */
public final class NotificationTemplates {

    private NotificationTemplates() {}

    public static NotificationContent welcome(String firstName) {
        return new NotificationContent(
                "Welcome to Emras! 🎉",
                String.format("""
                        Hi %s,
                        
                        Welcome to Emras — Bangladesh's favorite clothing shop.
                        
                        Start shopping at emras.com.bd
                        
                        The Emras Team
                        """, firstName),
                Notification.NotificationType.WELCOME
        );
    }

    public static NotificationContent orderConfirmed(Long orderId, BigDecimal amount) {
        return new NotificationContent(
                "Your order has been confirmed! ✅",
                String.format("""
                        Your order #%d has been confirmed.
                        
                        Total Amount: ৳%.2f
                        
                        We are preparing your order. You will receive
                        another notification once it ships.
                        
                        Thank you for shopping with Emras!
                        """, orderId, amount),
                Notification.NotificationType.ORDER_CONFIRMED
        );
    }

    public static NotificationContent paymentSuccess(Long orderId, BigDecimal amount,
                                                     String transactionId, String method) {
        return new NotificationContent(
                "Payment successful! ৳" + amount,
                String.format("""
                        Payment received for order #%d.
                        
                        Amount:         ৳%.2f
                        Method:         %s
                        Transaction ID: %s
                        
                        Your order is now being processed.
                        Keep this transaction ID for your records.
                        
                        Thank you!
                        """, orderId, amount, method, transactionId),
                Notification.NotificationType.PAYMENT_SUCCESS
        );
    }

    public static NotificationContent paymentFailed(Long orderId, String reason) {
        return new NotificationContent(
                "Payment failed for order #" + orderId,
                String.format("""
                        We were unable to process payment for order #%d.
                        
                        Reason: %s
                        
                        Please try again with a different payment method.
                        Your order has been cancelled.
                        
                        Need help? Contact support@emras.com.bd
                        """, orderId, reason),
                Notification.NotificationType.PAYMENT_FAILED
        );
    }

    public static NotificationContent orderCancelled(Long orderId, String reason) {
        return new NotificationContent(
                "Order #" + orderId + " has been cancelled",
                String.format("""
                        Your order #%d has been cancelled.
                        
                        Reason: %s
                        
                        If a payment was made, a refund will be
                        processed within 3-5 business days.
                        
                        The Emras Team
                        """, orderId, reason),
                Notification.NotificationType.ORDER_CANCELLED
        );
    }

    public static NotificationContent paymentRefunded(Long orderId, BigDecimal amount) {
        return new NotificationContent(
                "Refund initiated for order #" + orderId,
                String.format("""
                        A refund of ৳%.2f has been initiated
                        for order #%d.
                        
                        Refunds are processed within 3-5 business days
                        depending on your payment provider.
                        
                        The Emras Team
                        """, amount, orderId),
                Notification.NotificationType.PAYMENT_REFUNDED
        );
    }

    public static NotificationContent lowStockAlert(String sku, int currentStock, int threshold) {
        return new NotificationContent(
                "⚠️ Low Stock Alert: " + sku,
                String.format("""
                        ADMIN ALERT — Low stock warning.
                        
                        SKU:            %s
                        Current Stock:  %d units
                        Threshold:      %d units
                        
                        Please restock soon to avoid stockouts.
                        """, sku, currentStock, threshold),
                Notification.NotificationType.LOW_STOCK_ALERT
        );
    }

    public static NotificationContent outOfStockAlert(String sku) {
        return new NotificationContent(
                "🚨 Out of Stock: " + sku,
                String.format("""
                        ADMIN ALERT — Product is out of stock.
                        
                        SKU: %s
                        
                        This product has been marked as unavailable
                        on the website. Please restock immediately.
                        """, sku),
                Notification.NotificationType.OUT_OF_STOCK_ALERT
        );
    }

    public record NotificationContent(
            String title,
            String body,
            Notification.NotificationType type
    ) {}
}