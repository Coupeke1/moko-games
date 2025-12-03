package be.kdg.team22.checkersservice.infrastructure.game.redis;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import be.kdg.team22.checkersservice.infrastructure.game.redis.model.GameModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@Primary
public class RedisGameRepository implements GameRepository {
    private static final String KEY_PREFIX = "checkers:game:";
    private final RedisTemplate<String, GameModel> redisTemplate;

    public RedisGameRepository(RedisTemplate<String, GameModel> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(GameId gameId) {
        return KEY_PREFIX + gameId.value();
    }

    @Override
    public void save(Game game) {
        String key = key(game.id());

        redisTemplate.opsForValue().set(key, GameModel.fromDomain(game));
        redisTemplate.expire(key, 30,  TimeUnit.MINUTES);
    }

    @Override
    public void delete(GameId id) {
        redisTemplate.delete(key(id));
    }

    @Override
    public Optional<Game> findById(GameId id) {
        GameModel model =  redisTemplate.opsForValue().get(key(id));
        return Optional.ofNullable(model != null ? model.toDomain() : null);
    }
}
