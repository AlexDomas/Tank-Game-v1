package by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.units.BotTank;

public class BotEmitter {
    private BotTank[] bots;

    public BotTank[] getBots() {
        return bots;
    }

    public static final int MAX_BOTS_COUNT = 200;

    public BotEmitter(GameScreen gameScreen, TextureAtlas atlas) {
        this.bots = new BotTank[MAX_BOTS_COUNT];
        for (int i = 0; i < bots.length; i++) {
            this.bots[i] = new BotTank(gameScreen, atlas);
        }
    }

    public void activate(float x, float y) {
        for (int i = 0; i < bots.length; i++) {
            if (!bots[i].isActive()) {
                bots[i].activate(x, y);
                break;
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < bots.length; i++) {
            if (bots[i].isActive()) {
                bots[i].render(batch);
            }
        }
    }

    public void update(float dt) {   //проходим по все пулям, и если она активна, то обновляем её
        for (int i = 0; i < bots.length; i++) {
            if (bots[i].isActive()) {
                bots[i].update(dt);
            }
        }
    }
}
