/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.LinkedList;

/**
 *
 * @author CET
 */
public class openFileExplorer {

    String onlyPath = "C:\\diana";
    String selectPath = "/select," + onlyPath;
    LinkedList<String> list = new LinkedList<>();
    StringBuilder sb = new StringBuilder();
    boolean flag = true;

    public static void main(String []args) {
        openFileExplorer e = new openFileExplorer();
        e.reading();
    }
    void reading(){
        for (int k = 0; k < selectPath.length(); k++) {
            if (k == 0) {
                sb.append(selectPath.charAt(k));
                continue;
            }
            if (selectPath.charAt(k) == ' ' && flag) {
                list.add(sb.toString());
                sb.setLength(0);
                flag = false;
                continue;
            }
            if (!flag && selectPath.charAt(k) != ' ') {
                flag = true;
            }
            sb.append(selectPath.charAt(k));
        }
        list.add(sb.toString());
        list.addFirst("explorer.exe");

        list.forEach((s) -> {
            System.out.println("String: " + s);
        });
    }
}
