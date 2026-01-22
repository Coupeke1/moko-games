package be.kdg.team22.socialservice.api.friends;

import be.kdg.team22.socialservice.api.friends.models.AddFriendRequestModel;
import be.kdg.team22.socialservice.api.friends.models.FriendModel;
import be.kdg.team22.socialservice.application.friends.FriendService;
import be.kdg.team22.socialservice.domain.user.UserId;
import be.kdg.team22.socialservice.domain.user.Username;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {
    private final FriendService service;

    public FriendsController(final FriendService service) {
        this.service = service;
    }

    @GetMapping
    public List<FriendModel> getFriends(@AuthenticationPrincipal final Jwt token) {
        UserId user = UserId.get(token);
        return service.getFriends(user);
    }

    @GetMapping("/requests/incoming")
    public List<FriendModel> getIncomingRequests(@AuthenticationPrincipal final Jwt token) {
        UserId user = UserId.get(token);
        return service.getIncomingRequests(user);
    }

    @GetMapping("/requests/outgoing")
    public List<FriendModel> getOutgoingRequests(@AuthenticationPrincipal final Jwt token) {
        UserId user = UserId.get(token);
        return service.getOutgoingRequests(user);
    }

    @PostMapping
    public void addFriend(@AuthenticationPrincipal final Jwt token, @RequestBody final AddFriendRequestModel request) {
        UserId user = UserId.get(token);
        service.sendRequest(user, new Username(request.username()));
    }

    @PostMapping("/accept/{id}")
    public void accept(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId user = UserId.get(token);
        UserId otherUser = UserId.from(id);
        service.acceptRequest(user, otherUser);
    }

    @PostMapping("/reject/{id}")
    public void reject(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId user = UserId.get(token);
        UserId otherUser = UserId.from(id);
        service.rejectRequest(user, otherUser);
    }

    @PostMapping("/cancel/{id}")
    public void cancel(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId user = UserId.get(token);
        UserId otherUser = UserId.from(id);
        service.cancelRequest(user, otherUser);
    }

    @DeleteMapping("/remove/{id}")
    public void remove(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId user = UserId.get(token);
        UserId otherUser = UserId.from(id);
        service.removeFriend(user, otherUser);
    }
}