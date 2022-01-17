package by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.units;

import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.GameScreen;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.Item;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.TanksRpgGame;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.utils.Direction;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.utils.KeysControl;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.utils.TankOwner;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks.Weapon;

public class PlayerTank extends Tank {

    KeysControl keysControl;
    StringBuilder tmpString;
    GameScreen gameScreen;
    int index;
    int score;
    int lives;

    public PlayerTank(int index, GameScreen game, KeysControl keysControl, TextureAtlas atlas) {
        super(game);
        this.index = index;
        this.gameScreen = game;
        this.ownerType = TankOwner.PLAYER;
        this.keysControl = keysControl;
        this.weapon = new Weapon(atlas);
        this.texture = atlas.findRegion("playerTankBase");
        this.textureHp = atlas.findRegion("bar");
        this.position = new Vector2(40.0f, 110.0f);
        this.speed = 50.0f;
        this.width = texture.getRegionWidth();
        this.height = texture.getRegionHeight();
        this.hpMax = 10;
        this.hp = this.hpMax;
        this.circle = new Circle(position.x, position.y, (width + height) / 2);
        this.lives = 5;
        this.tmpString = new StringBuilder();
    }

    public void addScore(int amount) {
        score += amount;
    } //подсчет очков игрока

    @Override
    public void destroy() {
        lives--;
        if (lives > 0) {
            hp = hpMax;
        } else {
            hp = 0;
            lives = 0;
            speed = 0;

        }
    }

    public void update(float dt) {
        checkMovement(dt);
        if (keysControl.getTargeting() == KeysControl.Targeting.MOUSE) {
            rotateTurretToPoint(gameScreen.getMousePosition().x, gameScreen.getMousePosition().y, dt);
            if (Gdx.input.isTouched()) {
                fire();
            }
        } else {
            if (Gdx.input.isKeyPressed(keysControl.getRotateTurretLeft())) {
                turretAngle = Utils.makeRotation(turretAngle, turretAngle + 90.0f, 270.0f, dt);
                turretAngle = Utils.angleToFromNegPiToPosPi(turretAngle);
            }
            if (Gdx.input.isKeyPressed(keysControl.getRotateTurretRight())) {
                turretAngle = Utils.makeRotation(turretAngle, turretAngle - 90.0f, 270.0f, dt);
                turretAngle = Utils.angleToFromNegPiToPosPi(turretAngle);
            }
            if (Gdx.input.isKeyPressed(keysControl.getFire())) {
                fire();
            }

        }
        super.update(dt);
    }

    public void consumePowerUp(Item item) {
        switch (item.getType()) {
            case MEDKIT:
                hp += 4;
                if (hp > hpMax) {
                    hp = hpMax;
                }
                break;
            case SHIELD:
                addScore(1000);
                break;
        }
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font24) {
        tmpString.setLength(0);
        tmpString.append("Player: ").append(index);
        tmpString.append("\nScore: ").append(score);
        tmpString.append("\nLives: ").append(lives);
        if (lives <=0){
            tmpString.append("\nGAME OVER");
        }
        font24.draw(batch, tmpString, 20 + (index - 1) * 200, 700);

    }

    public void checkMovement(float dt) {
        if (Gdx.input.isKeyPressed(keysControl.getLeft())) {
            move(Direction.LEFT, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getRight())) {
            move(Direction.RIGHT, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getUp())) {
            move(Direction.UP, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getDown())) {
            move(Direction.DOWN, dt);
        }
    }
}
