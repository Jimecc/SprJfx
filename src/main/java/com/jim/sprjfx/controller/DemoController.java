package com.jim.sprjfx.controller;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.jim.sprjfx.SprJfxApplication;
import com.jim.sprjfx.handler.Serverhandler;
import com.jim.sprjfx.service.*;
import com.jim.sprjfx.view.MainView;
import com.jim.sprjfx.view.RegistView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;


@Slf4j
@Controller
@FXMLController
@RequestMapping(value="/shsw2")
public class DemoController {
    @Autowired
    private MsgController msgController;

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private DemoService demoService;

    @Autowired
    private SocketThread socketThread;




    private boolean flag = true;
    public boolean webStatus = false;
    public boolean socketStatus = true;

    public Button button_socket;
    public Button button_web;
    public Button button_submit;
    public Button button_logout;
    public Button button_newnumber;
    public Button button_submit_jsonAddress;
    public Label label_nums;
    public Label label_address_update_success;
    public Label label_address_update_fail;
    public TextField textField_JsonAddress;
    public TextField TextField_SendData;
    public Circle circle;
    public Label label_WebSocketType;
    public Button button_startRedis;
    public Button button_startSocket;


    RestOperations restTemplate = new RestTemplate();

    //    http://localhost:8081/shsw/json
    @PostMapping(value = "/jsonpost")
    @ResponseBody
    public String JSONPost(@RequestBody JSONObject jsonObject) {
        log.info("Post.JSON.Info--------------" + jsonService.JsonData(jsonObject,textField_JsonAddress.getText()));
        return "Success";
    }

    @RequestMapping(value = "/json")
    @ResponseBody
    public String JSONGet(@RequestBody JSONObject jsonObject) {
        log.info("Get/Post.JSON.Info--------------" + jsonService.JsonData(jsonObject,textField_JsonAddress.getText()));
        return "Success";
    }


    public void onMouseClicked_socket() {
        socketStatus = !socketStatus;
        if (socketStatus == false) {
            System.out.println("--关闭-------------------------------------------------------------");
            socketIOServer.addDisconnectListener(client -> {
                client.disconnect();
            });
        } else {
            socketIOServer.addDisconnectListener(client -> {
                client.disconnect();
            });
            System.out.println("--开启-------------------------------------------------------------");

        }
    }

    public void onMouseClicked_web() {
        webStatus = !webStatus;
    }

    public void onMouseMoved_button_socket() {
        if (socketStatus == true) {
            demoService.onMouseMoved_bg(button_socket,"#6495ED");
        } else {
            demoService.onMouseMoved_bg(button_socket,"#6495ED");
        }
    }

    public void onMouseClicked_button_submit_jsonAddress() {
        String url = textField_JsonAddress.getText();
        if (!"".equals(url)) {
            try {
                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
                linkedHashMap.put("testLink", "Success");
                //
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(linkedHashMap));
                log.info("newLinkTest--------- " + restTemplate.postForEntity(url, json, String.class).getBody());
                label_address_update_success.setTextFill(Paint.valueOf("#15c930"));
                label_address_update_success.setText("接口更新成功");
                label_address_update_fail.setText(null);
            } catch (Exception e) {
                label_address_update_success.setTextFill(Paint.valueOf("#ff3838"));
                label_address_update_success.setText("连接失败，请检查后重试");
                label_address_update_fail.setText(e.toString());
            }
        } else {
            label_address_update_success.setTextFill(Paint.valueOf("#ffc61c"));
            label_address_update_success.setText("接口清除成功");
            label_address_update_fail.setText(null);

        }
    }

    /**
     * 开发：hsk
     * 函数：onMouseMoved
     * 个数：*3
     * 功能：鼠标移动到按键上改变颜色
     */

    // Socket 按钮
    public void onMouseMoved_startSocket(){
        demoService.onMouseMoved_bg(button_startSocket,"#69B319");
    }

    // Redis 按钮（左下角）
    public void onMouseMoved_startRedis(){
        demoService.onMouseMoved_bg(button_startRedis,"#69B319");
    }

    // JSONAddress 提交url按钮
    public void onMouseMoved_button_submit_jsonAddress() {
        demoService.onMouseMoved_bg(button_submit_jsonAddress,"#6495ED");
    }

    // websocket 提交data按钮
    public void onMouseMoved_button_submit() {
        demoService.onMouseMoved_bg(button_submit,"#6495ED");
    }

    // layout 退出按钮
    public void onMouseMoved_button_logout() {
        demoService.onMouseMoved_bg(button_logout,"#FFDCD4");
    }

    // newUser 新建用户按钮
    public void onMouseMoved_button_newnumber() {
        demoService.onMouseMoved_bg(button_newnumber,"#B3E5FF");
    }

    // onAction 按下Enter == 确定
    public void onEnter_send_data(ActionEvent ae) {
        onMouseClicked_submit();
    }
    public void onAction_JsonAddress() {
        onMouseClicked_button_submit_jsonAddress();
    }

    public void onMouseClicked_submit() {
        String text = TextField_SendData.getText();
        String type = typeService.returnType(text);
        showView(type);
        label_WebSocketType.setText("数据类型为:  " + type);
        label_nums.setText(msgController.send(text));
    }

    public void onMouseClicked_startSocket() throws IOException, InterruptedException {
        socketThread.startSocket();
    }
    public void onMouseClicked_startRedis() {
        threadService.SetRedis("hskcool","coolhsk");
        threadService.ScanRedis("hskcool");
    }
    public void onMouseClicked_test() throws IOException {
//        socketThread.startServer();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void onMouseClicked_turnOff() throws IOException {
        socketThread.test();
    }
    public void onMouseClicked_JsonAddress() {
        label_address_update_success.setText(null);
        label_address_update_fail.setText(null);
    }

    public void onMouseClicked_sendWebsocketMessage() {
        label_WebSocketType.setText(null);
        label_nums.setText(null);
    }


    public void onMouseClicked_layout() {
        demoService.jumpWindows(520,380,MainView.class);
    }

    public void onMouseClicked_button_newnumber() {
        GUIState.getStage().centerOnScreen();
        SprJfxApplication.showView(RegistView.class);
    }


    public void showView(String type){
        if("pureChinese".equals(type)){
            demoService.moveCircle(160, 0, -30, 40, 20,circle);
        }else if("pureNumber".equals(type)){
            demoService.moveCircle(160, 0, 0, 0, 20,circle);
        }else if("pureString".equals(type)){
            demoService.moveCircle(0, 160, 0, 0, 20,circle);
        }else if("pureChar".equals(type)){
            demoService.moveCircle(160, 0, 30, -40, 20,circle);
        }else if("NULL".equals(type)){
            demoService.toBig(circle);
        }else if("notPure".equals(type)){
            demoService.moveCircle(0, 160, 30, -40, 20,circle);
        }

    }
    // 左→右
    public void moveCircleL2R() {
        demoService.moveCircle(0, 160, 0, 0, 20,circle);
    }



}
