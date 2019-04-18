package net.jetblack.feedbus.messages;

/**
 * The type of the message.
 */
public enum MessageType {
    MulticastData,
    UnicastData,
    ForwardedSubscriptionRequest,
    NotificationRequest,
    SubscriptionRequest,
    MonitorRequest
}
