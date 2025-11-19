package be.kdg.team22.socialservice.friends.api;

import be.kdg.team22.socialservice.friends.api.models.AddFriendRequestModel;
import be.kdg.team22.socialservice.friends.api.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.friends.application.FriendService;
import be.kdg.team22.socialservice.friends.domain.UserId;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendService friendService;

    public FriendsController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public FriendsOverviewModel getFriends(@AuthenticationPrincipal Jwt jwt) {
        UserId currentUser = UserId.fromJwt(jwt);
        return friendService.getOverview(currentUser);
    }

    @PostMapping("/add")
    public void addFriend(@AuthenticationPrincipal Jwt jwt,
                          @RequestBody AddFriendRequestModel request) {
        UserId currentUser = UserId.fromJwt(jwt);
        friendService.sendFriendRequest(currentUser, request.username());
    }

    @PostMapping("/accept/{id}")
    public void accept(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID id) {
        UserId currentUser = UserId.fromJwt(jwt);
        friendService.acceptRequest(currentUser, id);
    }

    @PostMapping("/reject/{id}")
    public void reject(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID id) {
        UserId currentUser = UserId.fromJwt(jwt);
        friendService.rejectRequest(currentUser, id);
    }

    @PostMapping("/cancel/{id}")
    public void cancel(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID id) {
        var currentUser = UserId.fromJwt(jwt);
        friendService.cancelRequest(currentUser, id);
    }

    @DeleteMapping("/remove/{id}")
    public void remove(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID id) {
        UserId currentUser = UserId.fromJwt(jwt);
        friendService.removeFriend(currentUser, id);
    }
}
