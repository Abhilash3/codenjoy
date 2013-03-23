import com.javatrainee.tanks.Construction;
import com.javatrainee.tanks.Field;
import com.javatrainee.tanks.Tanks;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class TanksTest {
    // Е: Итак, начнем с того, что у нас есть поле. Но его пока нет:)
    // 1. Уясним для начала, что игра будет называться "Tanks"
    // 2. Игра должа создаваться
    // 3. Должно создаваться поле.
    // 4. Поле должно будет иметь размер
    // .... Пока что хватит, не будем тратить время на раздумиЯ.
    private Tanks game = new Tanks(3);

    @Test
    public void shouldGame_beCreating() {
       assertNotNull(game);
   }

    @Test
    public void shouldBeFieldWhenGameStarted() {
        assertNotNull(game.getField());
    }

    @Test
    public void shouldFieldBeOfTypeField() {
        Field field = new Field(3);
        assertEquals(field.getClass(), game.getField().getClass());
    }
    private Field field = game.getField();
    @Test
    public void shouldFieldHasSize() {
        assertEquals(true, field.getSize() != 0);
    }

    @Test
    public void shouldFieldHasSize3WhenGameCreated() {
        Tanks game = new Tanks(3);
        assertEquals(3, game.getField().getSize());
    }

    @Test
    public void shouldFieldBeDrawable() {
        assertEquals("*********", game.drawFieldWithoutBorder());
    }

    @Test
    public void shouldDraw16ElementsOnScreen() {
        Tanks game = new Tanks(4);
        assertEquals("****************",game.drawFieldWithoutBorder());
    }

    @Test
    public void shouldDrawFieldWithBorder() {
        Tanks game = new Tanks(4);
        assertEquals("XXXXXX"
                         +"X****X"
                         +"X****X"
                         +"X****X"
                         +"X****X"
                         +"XXXXXX", game.drawField());
    }
   //Заминка. Надо на поле препятствия нарисовать. Покодим.

    private Construction construction = new Construction();
    @Test
    public void shouldBeConstruction_WhenGameCreated() {
        assertNotNull(construction);
    }
    @Test
    public void shouldConstructionBeDrawable() {
        assertNotNull(construction.drawConstruction());
    }

    @Test
    public void shouldConstructionBeDrawnAsSquare() {
        assertEquals("■", construction.drawConstruction());
    }

    @Test
    public void shouldConstructionBeCreatedInTheMiddleOfTheField() {
        int[] coordinatesOfTheMiddleOfTheField = {field.getSize() / 2, field.getSize() / 2};
        Construction construction = new Construction(field.getSize() / 2, field.getSize() / 2);
        assertEquals(Arrays.toString(coordinatesOfTheMiddleOfTheField), Arrays.toString(construction.getCoordinates()));
    }    //Какой-то нагруженный тест получакется
    //Выкрутились... но что-то подозрительное...
    //Было бы неплохо закоммититься
    //Мы отвалились от сети

}
