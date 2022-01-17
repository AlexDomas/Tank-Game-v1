package by.bntu.poisit.Domas.kursovaya.gdxtanks.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Map {
    public enum WallType {
        HARD(0, 5, true, false, false),
        SOFT(1, 3, true, false, false),
        INDESTRUCTABLE(2, 1, false, false, false),
        WATER(3,1,false, true, true),
        NONE(0, 0, false, true, true);

        int index; // индекс стены
        int maxHp;
        boolean unitPassable;
        boolean projectilePassable;
        boolean destructible; //можно ли уничтожить вообще стену

        WallType(int index, int maxHp, boolean destructible, boolean unitPassable, boolean projectilePassable) {
            this.index = index;
            this.maxHp = maxHp;
            this.destructible = destructible;
            this.unitPassable = unitPassable;
            this.projectilePassable = projectilePassable;
        }
    }

    private class Cell {
        WallType type;
        int hp;

        public Cell(WallType type) {
            this.type = type;
            this.hp = type.maxHp;
        }

        public void damage() {
            if (type.destructible) {
                hp--;
                if (hp <= 0) {
                    type = WallType.NONE; //сбросили тип в несуществующий
                }
            }
        }

        public void changeType(WallType type) {
            this.type = type;
            this.hp = type.maxHp;
        }
    }

    public static final int SIZE_X = 64;
    public static final int SIZE_Y = 36;

    public static final int CELL_SIZE = 20;

    private TextureRegion grassTexture; //ссылка на отдельный компонент текстуры
    private TextureRegion[][] wallsTexture; // двумерный массив регионов
    private Cell cells[][];


    public Map(TextureAtlas atlas) {
            this.wallsTexture = new TextureRegion(atlas.findRegion("obstacles")).split(CELL_SIZE, CELL_SIZE);
            this.grassTexture = atlas.findRegion("grass40");
            this.cells = new Cell[SIZE_X][SIZE_Y];
            for (int i = 0; i < SIZE_X; i++) {
                for (int j = 0; j < SIZE_Y; j++) {
                    cells[i][j] = new Cell(WallType.NONE);
                    int cx = i / 4;
                    int cy = j / 4;
                    if (cx % 2 == 0 && cy % 2 == 0) {
                        if(MathUtils.random() < 0.7f) {
//                        cells[i][j].changeType(WallType.HARD);
                            cells[i][j].changeType(WallType.WATER);
                        } else {
                            cells[i][j].changeType(WallType.SOFT);
                        }
                    }
                }
            }
        for (int i = 0; i < SIZE_X; i++) {  // снизу и сверху поставить непробиваемые стены
            cells[i][0].changeType(WallType.INDESTRUCTABLE);
            cells[i][SIZE_Y - 1].changeType(WallType.INDESTRUCTABLE);
        }

        for (int i = 0; i < SIZE_Y; i++) { // справа слева поставить непробиваемые стены
            cells[0][i].changeType(WallType.INDESTRUCTABLE);
            cells[SIZE_X - 1][i].changeType(WallType.INDESTRUCTABLE);
        }
    }

    public void checkWallAndBulletsCollision(Bullet bullet) { //обработка попадания пули в стену
        int cx = (int) (bullet.getPosition().x / CELL_SIZE);
        int cy = (int) (bullet.getPosition().y / CELL_SIZE);

        if (cx >= 0 && cy >= 0 && cx < SIZE_X && cy <= SIZE_Y) {
            if (!cells[cx][cy].type.projectilePassable) { // если он не проходим для снарядов, то мы по нему будем бить
                cells[cx][cy].damage();
                bullet.deactivate();
            }
        }
    }

    public boolean isAreaClear(float x, float y, float halfSize) { // проверка на возможность перемещения по определенным участкам карты, проверка на столкновение
        int leftX = (int) ((x - halfSize) / CELL_SIZE);
        int rightX = (int) ((x + halfSize) / CELL_SIZE);

        int bottomY = (int) ((y - halfSize) / CELL_SIZE);
        int topY = (int) ((y + halfSize) / CELL_SIZE);

        if (leftX < 0) { // проверка пределов объекта
            leftX = 0;
        }
        if (rightX >= SIZE_X) {
            rightX = SIZE_X - 1;
        }
        if (bottomY < 0) {
            bottomY = 0;
        }
        if (topY >= SIZE_Y) {
            topY = SIZE_Y - 1;
        }

        for (int i = leftX; i <= rightX; i++) { // если есть хотя бы одна преграда, то возвращаем false
            for (int j = bottomY; j <= topY; j++) {
                if (!cells[i][j].type.unitPassable) { //не проходим для персонажей
                    return false;
                }
            }
        }
        return true;
    }

    public void render(SpriteBatch batch) { //заполняем поле травой
        for (int i = 0; i < 1280 / 40; i++) {
            for (int j = 0; j < 720 / 40; j++) {
                batch.draw(grassTexture, i * 40, j * 40);
            }
        }

        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                if (cells[i][j].type != WallType.NONE) {
                    batch.draw(wallsTexture[cells[i][j].type.index][cells[i][j].hp - 1], i * CELL_SIZE, j * CELL_SIZE);
                }
            }
        }
    }
}
