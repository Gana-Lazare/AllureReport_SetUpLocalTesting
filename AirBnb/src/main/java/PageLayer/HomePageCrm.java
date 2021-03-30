package PageLayer;

import Base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Colors;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import javax.swing.text.Highlighter;

import static org.openqa.selenium.support.Colors.RED;

public class HomePageCrm extends Base {

    @FindBy(how = How.XPATH,using = "//*[@aria-label=\"Page d'accueil Airbnb\"]")
    public static WebElement logo;

    public void test(){
     
    }


}
