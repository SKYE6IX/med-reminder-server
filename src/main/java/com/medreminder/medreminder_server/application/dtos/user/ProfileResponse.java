package com.medreminder.medreminder_server.application.dtos.user;

public record ProfileResponse(String id, String imageUrl, String name, String relation, boolean isSelf) {
}
