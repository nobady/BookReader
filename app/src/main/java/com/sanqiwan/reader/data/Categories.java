package com.sanqiwan.reader.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 9/24/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Categories {
    public static Map<Integer, List<CategoryItem>> categories = new HashMap<Integer, List<CategoryItem>>();

    public static final int CATEGORY_CITY = 43;
    public static final int CATEGORY_ANCIENT = 44;
    public static final int CATEGORY_ACROSS = 54;
    public static final int CATEGORY_FANTASY = 48;

    static {
        List<CategoryItem> cityCategoryItems = new ArrayList<CategoryItem>();
        cityCategoryItems.add(new CategoryItem("完结合集", 0, 0));
        cityCategoryItems.add(new CategoryItem("校园纯爱", 193, 1));
        cityCategoryItems.add(new CategoryItem("职场情缘", 102, 2));
        cityCategoryItems.add(new CategoryItem("豪门恩怨", 100, 3));
        cityCategoryItems.add(new CategoryItem("婚里婚外", 170, 4));
        cityCategoryItems.add(new CategoryItem("娱乐明星", 194, 5));
        cityCategoryItems.add(new CategoryItem("民国世家", 195, 6));
        cityCategoryItems.add(new CategoryItem("辣妈萌宝", 196, 7));
        categories.put(CATEGORY_CITY, cityCategoryItems);

        List<CategoryItem> AncientCategoryItems = new ArrayList<CategoryItem>();
        AncientCategoryItems.add(new CategoryItem("完结合集", 0, 0));
        AncientCategoryItems.add(new CategoryItem("市井江湖", 183, 1));
        AncientCategoryItems.add(new CategoryItem("种田经商", 181, 2));
        AncientCategoryItems.add(new CategoryItem("宅门世家", 172, 3));
        AncientCategoryItems.add(new CategoryItem("宫闱情仇", 112, 4));
        AncientCategoryItems.add(new CategoryItem("帝王将相", 111, 5));
        AncientCategoryItems.add(new CategoryItem("民国情缘", 184, 6));
        AncientCategoryItems.add(new CategoryItem("女尊王朝", 185, 7));
        categories.put(CATEGORY_ANCIENT, AncientCategoryItems);

        List<CategoryItem> AcrossCategoryItems = new ArrayList<CategoryItem>();
        AcrossCategoryItems.add(new CategoryItem("完结合集", 0, 0));
        AcrossCategoryItems.add(new CategoryItem("种田宅斗", 188, 1));
        AcrossCategoryItems.add(new CategoryItem("穿越武侠", 187, 2));
        AcrossCategoryItems.add(new CategoryItem("宫廷王侯", 186, 3));
        AcrossCategoryItems.add(new CategoryItem("女尊女强", 189, 4));
        AcrossCategoryItems.add(new CategoryItem("异世囧途", 190, 5));
        AcrossCategoryItems.add(new CategoryItem("穿越民国", 191, 6));
        categories.put(CATEGORY_ACROSS, AcrossCategoryItems);

        List<CategoryItem> FantasyCategoryItems = new ArrayList<CategoryItem>();
        FantasyCategoryItems.add(new CategoryItem("完结合集", 0, 0));
        FantasyCategoryItems.add(new CategoryItem("异能修真", 153, 1));
        FantasyCategoryItems.add(new CategoryItem("仙侠情缘", 177, 2));
        FantasyCategoryItems.add(new CategoryItem("妖精灵怪", 199, 3));
        FantasyCategoryItems.add(new CategoryItem("西方魔幻", 179, 4));
        FantasyCategoryItems.add(new CategoryItem("随身空间", 200, 5));
        FantasyCategoryItems.add(new CategoryItem("未来幻想", 201, 6));
        categories.put(CATEGORY_FANTASY, FantasyCategoryItems);
    }

    private static final String CITY = "city";
    private static final String ANCIENT = "ancient";
    private static final String FANTASY = "fantasy";
    private static final String ACROSS = "across";

    public static String getCategory(int categoryId) {
        if (categoryId == CATEGORY_CITY) {
            return CITY;
        } else if (categoryId == CATEGORY_ANCIENT) {
            return ANCIENT;
        } else if (categoryId == CATEGORY_FANTASY) {
            return FANTASY;
        } else {
            return ACROSS;
        }
    }

    public static String getCategoryItem(int categoryId, int itemId) {
        return getCategory(categoryId) + getItemId(itemId);
    }

    private static String getItemId(int itemId) {
        String item = null;
        if (itemId == 101) {
            item = "_jingjingxiaoyuan";
        } else if (itemId == 102) {
            item = "_zhichangqingyuan";
        } else if (itemId == 173) {
            item = "_xiangjianqingshi";
        } else if (itemId == 100) {
            item = "_haomengenyuan";
        } else if (itemId == 170) {
            item = "_hunlihunwai";
        } else if (itemId == 104) {
            item = "_wangluoqianqing";
        } else if (itemId == 105) {
            item = "_yiguoliange";
        } else if (itemId == 106) {
            item = "_heibailiangdao";
        } else if (itemId == 180) {
            item = "_yitanfengyun";
        } else if (itemId == 171) {
            item = "_shijingminsheng";
        } else if (itemId == 181) {
            item = "_qinlouchuguan";
        } else if (itemId == 172) {
            item = "_zhaimenshijia";
        } else if (itemId == 112) {
            item = "_gongweiqingchou";
        } else if (itemId == 111) {
            item = "_diwangjiangxiang";
        } else if (itemId == 113) {
            item = "_guochoujiahen";
        } else if (itemId == 148) {
            item = "_jianghufengyun";
        } else if (itemId == 150) {
            item = "_yijiehuanxiang";
        } else if (itemId == 177) {
            item = "_xianxiaqingyuan";
        } else if (itemId == 178) {
            item = "_yaojinglingguai";
        } else if (itemId == 179) {
            item = "_xifangmohuan";
        } else if (itemId == 152) {
            item = "_xixuejiazu";
        } else if (itemId == 0) {
            item = "_wanjieheji";
        }
        return item;
    }
}