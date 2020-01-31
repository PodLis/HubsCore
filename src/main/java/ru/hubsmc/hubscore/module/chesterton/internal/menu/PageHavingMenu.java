package ru.hubsmc.hubscore.module.chesterton.internal.menu;

public interface PageHavingMenu {

    int getMaxPage();

    int getPage();

    PageHavingMenu getPrevMenu();

    void setPrevMenu(PageHavingMenu prevMenu);

    PageHavingMenu getNextMenu();

    void setNextMenu(PageHavingMenu nextMenu);

}
