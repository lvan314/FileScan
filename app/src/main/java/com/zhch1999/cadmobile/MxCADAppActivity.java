/****************************************************************************
 Copyright (c) 2008-2010 Ricardo Quesada
 Copyright (c) 2010-2012 cocos2d-x.org
 Copyright (c) 2011      Zynga Inc.
 Copyright (c) 2013-2014 Chukong Technologies Inc.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package com.zhch1999.cadmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.MxDraw.McDbArc;
import com.MxDraw.McDbAttribute;
import com.MxDraw.McDbBlockReference;
import com.MxDraw.McDbBlockTable;
import com.MxDraw.McDbBlockTableRecord;
import com.MxDraw.McDbCircle;
import com.MxDraw.McDbCurve;
import com.MxDraw.McDbDatabase;
import com.MxDraw.McDbDictionary;
import com.MxDraw.McDbEllipse;
import com.MxDraw.McDbEntity;
import com.MxDraw.McDbLayerTable;
import com.MxDraw.McDbLayerTableRecord;
import com.MxDraw.McDbLine;
import com.MxDraw.McDbMText;
import com.MxDraw.McDbMxImageMark;
import com.MxDraw.McDbObject;
import com.MxDraw.McDbPoint;
import com.MxDraw.McDbPolyline;
import com.MxDraw.McDbRasterImage;
import com.MxDraw.McDbSpline;
import com.MxDraw.McDbText;
import com.MxDraw.McDbTextStyleTable;
import com.MxDraw.McDbTextStyleTableRecord;
import com.MxDraw.McDbXrecord;
import com.MxDraw.McGeMatrix3d;
import com.MxDraw.McGePoint3d;
import com.MxDraw.McGeVector3d;
import com.MxDraw.MrxDbgSelSet;
import com.MxDraw.MrxDbgUiPrPoint;
import com.MxDraw.MrxDbgUtils;
import com.MxDraw.MxDrawActivity;
import com.MxDraw.MxDrawDragEntity;
import com.MxDraw.MxDrawWorldDraw;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;
import com.MxDraw.MxResbuf;

import org.cocos2dx.lib.Cocos2dxEditBox;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.ResizeLayout;

public class MxCADAppActivity extends MxDrawActivity {
    protected boolean m_isLoadAndroidLayoutUi = false;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        m_isLoadAndroidLayoutUi = true;
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mFile = extras.getString("file");
            if (!mFile.isEmpty()) {
                MxFunction.openFile(mFile);
            }
        }
    }

    /**
     * 手机按键按下
     * @param iKeyCode
     */
    @Override
    public void onKeyReleased(int iKeyCode) {
        Log.e("keyCode",iKeyCode+"");
        //6 -返回
        if (iKeyCode == 6) {
            /**
             * 开始执行命令 命令名称为string类型
             */
            MxFunction.sendStringToExecute("Mx_StartPage");//跳转到开页
        }
    }

    /**
     * 命令执行结束事件
     * @param sCmd
     */
    @Override
    public void commandEnded(String sCmd) {
        if (sCmd.equals("Mx_DrawFree")
                || sCmd.equals("Mx_InsertText")
        ) {
            long[] aryId = MxFunction.getEntitysLastCmd();
            if (aryId == null)
                return;
            for (int j = 0; j < aryId.length; j++) {
                String sName = MxFunction.getTypeName(aryId[j]);
                if (sName.equals("McDbPolyline")) {
                    McDbPolyline pl = new McDbPolyline(aryId[j]);
                    double dA = pl.getArea();
                    String sA;
                    sA = String.format("Area:%f", dA);
                    Log.e("McDbPolyline Area:", sA);
                    for (int i = 0; i < pl.numVerts(); i++) {
                        McGePoint3d pt = pl.getPointAt(i);
                        double dBulge = pl.getBulgeAt(i);
                        String sT2;
                        sT2 = String.format("pt:%f,%f,%f,dBulge:%f", pt.x, pt.y, pt.z, dBulge);
                        Log.e("McDbPolyline Point:", sT2);
                        if (dBulge > 0.001) {
                            McGePoint3d pt2;
                            if (i == pl.numVerts() - 1) {
                                pt2 = pl.getPointAt(0);
                            } else {
                                pt2 = pl.getPointAt(i + 1);
                            }

                            double[] arc = MxFunction.calcArc(pt.x, pt.y, pt2.x, pt2.y, dBulge);
                            if (arc != null) {
                                String sTem = String.format("cen:%f,%f,dR:%f,dS:%f,dE:%f", arc[0], arc[1], arc[2], arc[3], arc[4]);

                                Log.e("Arc:", sTem);
                            }
                        }
                    }
                } else if (sName.equals("McDbText")) {
                    McDbText txt = new McDbText(aryId[j]);
                    String sTem = txt.textString();
                    Log.e("txt:", sTem);
                }
            }
        }
    }

    /**
     * 保存预览完成事件
     * @param isOK
     */
    @Override
    public void savePreviewFileComplete(boolean isOK) {
        Log.e("savePreviewFileComplete", "");
    }

    /**
     * 打开完成时调用的事件
     * @param isOpenSucces
     */
    @Override
    public void openComplete(boolean isOpenSucces) {
        String sT;
        sT = String.format("openComplete:%d", isOpenSucces ? 1 : 0);
        Log.e("openComplete", sT);
    }

    /**
     * 初始化完成时调用的事件
     */
    @Override
    public void initComplete() {
        Log.e("initComplete", "");
    }

    /**
     * 拖放过程中的动态绘制回调事件.
     * @param draw 绘制对象
     * @param dragData 动态拖动数据.
     * @return
     */
    @Override
    public boolean dynWorldDraw(MxDrawWorldDraw draw, MxDrawDragEntity dragData) {
        if (dragData.GetGuid().equals("mydyndraw")) {
            // 交互画直线的动态绘制。
            // 取到动态绘制数据。
            String sPrv = dragData.GetString("Prv");
            McGePoint3d pt2 = dragData.GetDragCurrentPoint();
            McGePoint3d pt1 = dragData.GetPoint("pt1");
            // 算出，动态距离。
            double dDist = pt1.distanceTo(pt2);
            McGeVector3d vec = pt2.SumVector(pt1);
            double dAng = vec.angleTo(McGeVector3d.kXAxis, McGeVector3d.kNZAxis);
            vec.Mult(0.5);
            pt1.Add(vec);
            String sT;
            sT = sPrv + "=" + String.format("%f", dDist);
            double dH = MxFunction.viewLongToDoc(30);
            // 在两点的中心点，动态绘制一个文本。
            draw.DrawText(pt1.x, pt1.y, sT, dH, dAng, 1, 1);
            // draw.DrawLine(0,0,100,100);
            return true;
        } else if (dragData.GetGuid().equals("mydyngetpoing")) {
            McGePoint3d currentPoint = dragData.GetDragCurrentPoint();
            String sT;
            sT = String.format("pt:%f,%f,%f", currentPoint.x, currentPoint.y, currentPoint.z);
            Log.e("currentPoint", sT);
        }
        return false;
    }

    /**
     * 对象的夹点被编辑完成后,会调用该事件
     * @param lId 当前被选择的实体lId
     * @param lGripIndex 被编辑的夹点索引
     */
    @Override
    public void objectGripEdit(long lId, int lGripIndex) {
        String sT;
        sT = String.format(" lId:%d,%d", lId, lGripIndex);
        Log.e("objectGripEdit", sT);
    }

    /**
     * 当前选择实体发生变化,会调用该事件
     * @param lId 当前被选择的实体lId,是一个实体lid链表
     */
    @Override
    public void selectModified(long lId) {
    }

    /**
     * 命令调用事件
     * @param iCommand
     */
    @Override
    public void commandEvent(int iCommand) {
        if (iCommand == 1) {
            //MxFunction.zoomAll();
            //MxFunction.sendStringToExecute("MT_TestTip");\
            String sFileName = MxFunction.getWorkDir() + "/TestWirte.dwg";
            MxFunction.writeFile(sFileName);
        } else if (iCommand == 2) {
            String sFileName = MxFunction.getWorkDir() + "/总图.dwg";
            MxFunction.openFileEx(sFileName, ReadContent.kReadxData);
        } else if (iCommand == 3) {
            MxFunction.openFile("");
        } else if (iCommand == 4) {
            MrxDbgSelSet ss = new MrxDbgSelSet();
            ss.allSelect();
            for (int i = 0; i < ss.size(); i++) {
                long lId = ss.at(i);
                McDbEntity ent = new McDbEntity(lId);
                // 得到对象的层名.
                Log.e("LayerName", ent.layerName());
                String sName = MxFunction.getTypeName(lId);
                if (sName.equals("McDbLine")) {
                    McDbLine line = new McDbLine(ss.at(i));
                    McGePoint3d sPt = line.getStartPoint();
                    McGePoint3d ePt = line.getEndPoint();
                    String sT;
                    sT = String.format("sPt:%f,%f,%f,ePt:%f,%f,%f", sPt.x, sPt.y, sPt.z, ePt.x, ePt.y, ePt.z);
                    Log.e("Linedata", sT);
                } else if (sName.equals("McDbCircle")) {
                    McDbCircle cir = new McDbCircle(ss.at(i));
                    McGePoint3d cen = cir.getCenter();
                    double fR = cir.getRadius();
                    String sT;
                    sT = String.format("cen:%f,%f,r:%f", cen.x, cen.y, fR);
                    Log.e("Circledata", sT);
                } else if (sName.equals("McDbPoint")) {
                    McDbPoint point = new McDbPoint(ss.at(i));
                    McGePoint3d pos = point.position();
                    String sT;
                    sT = String.format("Point pos:%f,%f", pos.x, pos.y);
                    Log.e("McDbPoint", sT);
                } else if (sName.equals("McDbText")) {
                    McDbText txt = new McDbText(ss.at(i));
                    McGePoint3d pos = txt.position();
                    String sTxt = txt.textString();
                    double dH = txt.height();
                    String sT;
                    sT = String.format(" pos:%f,%f,Txt:%s,H:%f", pos.x, pos.y, sTxt, dH);
                    Log.e("McDbText", sT);
                } else if (sName.equals("McDbMText")) {
                    McDbMText txt = new McDbMText(ss.at(i));
                    McGePoint3d pos = txt.location();
                    String sTxt = txt.contents();
                    double dH = txt.textHeight();
                    String sT;
                    sT = String.format(" pos:%f,%f,Txt:%s,H:%f", pos.x, pos.y, sTxt, dH);
                    Log.e("McDbMText", sT);
                } else if (sName.equals("McDbEllipse")) {
                    McDbEllipse ellipse = new McDbEllipse(ss.at(i));
                    McGePoint3d cen = ellipse.center();
                    McGeVector3d major = ellipse.majorAxis();
                    double radius = ellipse.radiusRatio();
                    double sang = ellipse.startAngle();
                    double eang = ellipse.endAngle();
                    String sT;
                    sT = String.format(" cen:%f,%f,major:%f,%f,radius:%f,sang:%f,eang:%f", cen.x, cen.y, major.x, major.y, radius, sang, eang);
                    Log.e("McDbEllipse", sT);
                } else if (sName.equals("McDbBlockReference")) {
                    McDbBlockReference blkRef = new McDbBlockReference(lId);
                    McDbBlockTableRecord blkRec = new McDbBlockTableRecord(blkRef.blockTableRecord());
                    Log.e("BlkName:", blkRec.getName());
                    long[] allAtt = blkRef.getAllAttribute();
                    if (allAtt != null) {
                        for (int j = 0; j < allAtt.length; j++) {
                            McDbAttribute att = new McDbAttribute(allAtt[j]);
                            Log.e("tagConst:", att.tagConst());
                            Log.e("textString:", att.textString());
                        }
                    }
                }
            }
        } else if (iCommand == 5) {
            //long lImageId = MxFunction.drawImage("start.png",100,100,1);
            // Mxunction.drawImageMarkEx("location.png",100,100,5,MxFunction.ImageAttachment.kMiddleCenter);
            MxFunction.openCurrentLayer();
            long lId = MxFunction.drawImageMarkEx("location2.png", 19354.596193, 19813.267774, 0.5, MxFunction.ImageAttachment.kBottomCenter);
            McDbMxImageMark image = new McDbMxImageMark(lId);
            image.setAngel(80 * 3.14159265 / 180.0);
            MxLibDraw.drawLine(19354.596193, 19813.267774, 19813.267774 + 2000, 19813.267774 + 200);
        } else if (iCommand == 6) {
            MxFunction.zoomAll();
        } else if (iCommand == 7) {
            MrxDbgSelSet ss = new MrxDbgSelSet();
            // 与用户交到在图上选择对象。
            ss.userSelect();
            String sT;
            sT = String.format("size:%d", ss.size());
            // 提示选择的对象.
            Log.e("userSelect", sT);
        } else if (iCommand == 8) {
            MxFunction.openFile("");
            // 设置画图颜色.
            long[] rgb = new long[3];
            rgb[0] = 255;
            rgb[1] = 0;
            rgb[2] = 0;

            MxLibDraw.setDrawColor(rgb);

            MxLibDraw.setLineWidth(10);
            MxLibDraw.addLayer("TestLayer");
            MxLibDraw.setLayerName("TestLayer");
            MxLibDraw.drawLine(10, 10, 200, 300);
            MxLibDraw.addLinetype("MyLine", "20,-10", 1);
            MxLibDraw.setLineType("MyLine");
            MxLibDraw.setLineWidth(5);
            long lId = MxLibDraw.drawLine(10, 300, 200, 10);
            McDbObject obj = new McDbObject(lId);
            // 创建对象扩展字典
            obj.createExtensionDictionary();
            // 得到扩展字典
            McDbDictionary dict = new McDbDictionary(obj.extensionDictionary());
            // 向扩展字典中加入一个扩展记录.
            McDbXrecord xrec = new McDbXrecord(dict.addRecord("MyData"));
            // 设置扩展记录数据。
            MxResbuf data = new MxResbuf();
            data.addLong(111);
            data.addString("xxxxxxx");
            xrec.setFromRbChain(data);
            // 测试求最近点函数。
            McDbCurve curve = new McDbCurve(lId);
            McGePoint3d pt = new McGePoint3d(100, 100, 0);
            McGePoint3d onPt = curve.getClosestPointTo(pt);
            if (onPt != null) {
                // 得到最近点.
                String sT;
                sT = String.format("onPt:%f,%f,%f", onPt.x, onPt.y, onPt.z);
                Log.e("onPt", sT);
            }
            MxFunction.zoomAll();
        } else if (iCommand == 9) {
            // 交互绘直线.
            //DynDrawLine();

        } else if (iCommand == 10) {
            long lId = MrxDbgUtils.selectEnt("点击选择对象:");
            if (lId != 0) {
                //McDbEntity ent = new McDbEntity (lId);
                //long[] newIds = ent.explode();
                //MxFunction.erase(lId);
                String sT;
                sT = String.format("selectEnt lId:%d", lId);
                Log.e("selectEnt", sT);
                McDbEntity ent = new McDbEntity(lId);
                // 得到对象的层名.
                String sLayer = ent.layerName();
                Log.e("LayerName", sLayer);
                MxResbuf xdata = ent.xData("");
                if (xdata != null) {

                    long lCount = xdata.getCount();

                    xdata.print();
                }

                long lDictId = ent.extensionDictionary();
                printDictionary(lDictId);
                // final long lEraseId = lId;
                //  this.runOnGLThread(new Runnable() {
                //          @Override
                //        public void run() {
                //             MxFunction.deleteObject(lEraseId);
                //           }
                //     });

            }
        } else if (iCommand == 11) {
            long[] ids = MxFunction.getAllLayer();
            if (ids == null)
                return;
            for (int i = 0; i < ids.length; i++) {
                McDbLayerTableRecord layer = new McDbLayerTableRecord(ids[i]);
                String sName = layer.getName();
                Log.e("LayerName:", sName);

                //layer.setIsOff(true);
            }
        } else if (iCommand == 12) {
            MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
            getPoint.setMessage("点取文字插入点");
            if (getPoint.go() != MrxDbgUiPrPoint.Status.kOk) {
                return;
            }
            McGePoint3d pt = getPoint.value();
            MxLibDraw.addTextStyle1("MyTextStyle", "txt.shx", "gbcbig.shx", 0.7f);
            MxLibDraw.setTextStyle("MyTextStyle");
            MxLibDraw.addTextStyle1("MyTextStyle2", "txt.shx", "hztxt.shx", 0.7f);
            MxLibDraw.drawText(pt.x, pt.y, 500, "测试Test");
            long lId = MxLibDraw.drawText(pt.x + 600, pt.y, 500, "测试Test2222");
            McDbText txt = new McDbText(lId);
            txt.setTextStyleName("MyTextStyle2");
        } else if (iCommand == 13) {
            MxFunction.openFile("");
            MxLibDraw.drawCircle(10, 10, 100);
            MxLibDraw.drawArc(10, 10, 200, 0, 45 * 3.14159265 / 180.0);
            MxFunction.zoomAll();
        } else if (iCommand == 14) {
            MxFunction.openFile("");
            MxLibDraw.pathMoveTo(10, 10);
            MxLibDraw.pathLineTo(10, 20);
            //MxLibDraw.pathLineToEx(20,20,2,1,0.1);
            MxLibDraw.pathLineTo(20, 20);
            MxLibDraw.pathLineTo(20, 10);
            MxLibDraw.drawPathToPolyline();
            MxFunction.zoomAll();
        } else if (iCommand == 15) {
            MxFunction.openFile("");
            MxLibDraw.pathMoveTo(10, 10);
            MxLibDraw.pathLineTo(10, 20);
            //MxLibDraw.pathLineToEx(20,20,2,1,0.1);
            MxLibDraw.pathLineTo(20, 20);
            MxLibDraw.pathLineTo(20, 10);
            MxLibDraw.drawPathToSpline();
            MxFunction.zoomAll();
        } else if (iCommand == 16) {
            printDictionary(MxFunction.getNamedObjectsDictionary());
        } else if (iCommand == 17) {
            MxFunction.newFile();
            Log.e("isModifyed", MxFunction.isModifyed() ? "Y" : "N");
            //String sFileName = MxFunction.getWorkDir() + "/tree.dwg";
            String sFileName = MxFunction.getWorkDir() + "/tk.dwg";
            String sBlkName = "tree";
            MxLibDraw.insertBlock(sFileName, sBlkName);
            // drawBlockReference(double dPosX, double dPosY, String pszBlkName, double dScale, double dAng);
            MxLibDraw.drawBlockReference(0, 0, "ZZCSTK_A3标准图框", 1, 0);
            MxFunction.zoomAll();
            Log.e("isModifyed", MxFunction.isModifyed() ? "Y" : "N");
        } else if (iCommand == 18) {
            // long  drawEllipse(double dCenterX, double dCenterY, double dMajorAxisX, double dMajorAxisY, double dRadiusRatio);
            MxFunction.newFile();
            MxLibDraw.drawEllipse(0, 0, 100, 100, 0.5);
            MxLibDraw.drawEllipseArc(200, 0, 100, 100, 0.5, 15 * 3.14159265 / 180, 90 * 3.14159265 / 180);
            MxFunction.zoomAll();
        } else if (iCommand == 19) {
            // 得到所有图层名称
            McDbLayerTable layerTable = MxFunction.getCurrentDatabase().getLayerTable();
            long[] allLayerId = layerTable.getAll();
            for (int i = 0; i < allLayerId.length; i++) {
                McDbLayerTableRecord laryRec = new McDbLayerTableRecord(allLayerId[i]);
                Log.e("LayerName:", laryRec.getName());
            }
        } else if (iCommand == 20) {
            // 得到所有文字样式名称
            McDbTextStyleTable txtstyleTable = MxFunction.getCurrentDatabase().getTextstyle();
            long[] allId = txtstyleTable.getAll();
            for (int i = 0; i < allId.length; i++) {
                McDbTextStyleTableRecord textStyleRecord = new McDbTextStyleTableRecord(allId[i]);
                Log.e("textStyleRecord:", textStyleRecord.getName());
                Log.e("fileName:", textStyleRecord.fileName());
                Log.e("bigFontFileName:", textStyleRecord.bigFontFileName());

            }
        } else if (iCommand == 55) {
            Log.e("custom toolbar:", "55");
        } else if (iCommand == 65) {
            Log.e("custom toolbar:", "65");
        } else if (iCommand == 21) {
            //TestMxDraw();
        } else if (iCommand == 22) {
            //DynCreateBlock();
        } else if (iCommand == 25) {
            String mJiFanName = "add";
            MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
            getPoint.setMessage("点取插入点");
            if (getPoint.go() != MrxDbgUiPrPoint.Status.kOk) {
                return;
            }
            McGePoint3d pt = getPoint.value();
            String sFileName = String.format("%s/%s.dwg", MxFunction.getWorkDir(), mJiFanName);
            String sBlkName = String.format("机房_%s", mJiFanName);
            MxLibDraw.insertBlock(sFileName, "TempBlkName");
            long lId = MxLibDraw.drawBlockReference(pt.x, pt.y, sBlkName, 0.01, 0);
            if (lId != 0) {
                McDbBlockReference blkRef = (McDbBlockReference) MxFunction.objectIdToObject(lId);
                McGePoint3d pos = blkRef.position();
                pos.z = 100;
                blkRef.setPosition(pos);
            }

        } else if (iCommand == 26) {
           // DoTest();
        } else if (iCommand == 27) {
            //DoPolyline();
        }
    }

    /**
     * 动态创建图块
     */
//    public void DynCreateBlock() {
//        // 得到块表
//        McDbBlockTable blkTab = MxFunction.getCurrentDatabase().getBlockTable();
//        // 添加一个块表记录,块名为空，是个匿名块
//        long lBlkRec = blkTab.add("");
//        McDbBlockTableRecord blkRec = new McDbBlockTableRecord(lBlkRec);
//        // 绘制直线
//        long lLine = MxLibDraw.drawLine(100, 100, 200, 200);
//        long lCircle = MxLibDraw.drawCircle(100, 100, 30);
//        // 把绘制直线加到块表记录中.
//        long lNewId = blkRec.addCloneEntity(lLine);
//        blkRec.addCloneEntity(lCircle);
//        // 删除之前临时画的对象。
//        MxFunction.deleteObject(lLine);
//        MxFunction.deleteObject(lCircle);
//        // 绘制块引用，引用刚才做的匿名块
//        long lIdBlkRef = MxLibDraw.drawBlockReference2(50, 50, lBlkRec, 1, 0);
//        McDbBlockReference blkRef = new McDbBlockReference(lIdBlkRef);
//        McDbAttribute attrib = blkRef.appendAttribute();
//        McGePoint3d pt = new McGePoint3d(50, 50, 0);
//        attrib.setPosition(pt);
//        attrib.setAlignmentPoint(pt);
//        attrib.setTextString("TestAttrib");
//        attrib.setHeight(30);
//        attrib.setInvisible(false);
//        blkRef.assertWriteEnabled();
//
//
//        MxFunction.zoomAll();
//    }

    /**
     * 打印字典
     * @param lId
     */
    public void printDictionary(long lId) {
        if (lId == 0)
            return;
        McDbDictionary dict = new McDbDictionary(lId);

        long[] all = dict.getAll();
        if (all == null)
            return;

        for (int i = 0; i < all.length; i++) {
            String sType = MxFunction.getTypeName(all[i]);
            String sName = dict.getName(all[i]);
            Log.e("Name", sName);

            if (sType.equals("McDbDictionary")) {
                printDictionary(all[i]);
            } else if (sType.equals("McDbXrecord")) {
                McDbXrecord xRec = new McDbXrecord(all[i]);
                MxResbuf data = xRec.rbChain();
                if (data != null)
                    data.print();

            }
        }


    }

    /**
     * 退出cad时触发
     * @return
     */
    @Override
    public boolean returnStart() {
        //MxFunction.newFile();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        /*
        this.runOnGLThread(new Runnable() {
            @Override
            public void run() {
               //....
            }
        });
*/

        return true;
    }

    /**
     * unicode 数据转换
     * @param theString
     * @return
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        char aChar2;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                aChar2 = theString.charAt(x++);
                if (aChar == 'U'
                        && aChar2 == '+'
                ) {
                    // Read the xxxx


                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 触摸事件定义
     * @param iType 触摸类型
     * @param dX 触摸x坐标
     * @param dY 触摸y坐标
     * @return
     */
    @Override
    public int touchesEvent(int iType, double dX, double dY) {
        double[] ret = MxFunction.docToView(dX, dY);
        String sK;
        sK = String.format("docToView:%f,%f", ret[0], ret[1]);
        Log.e("docToView", sK);
        //if(iType == 0)
        if (false) {
            long lImageId = MxFunction.drawImage("start.png", dX, dY, 30);
            McDbRasterImage image = new McDbRasterImage(lImageId);
            double dAng = image.rotation();
            image.setRotation(15 * 3.14159265 / 180.0);
        }
        // 在点击事件上，得到点的对象。

        //if(iType == EventType.kLongPressed)
        if (false) {


            String sT;
            sT = String.format("touchesEvent:%f,%f", dX, dY);
            Log.e("touchesEvent", sT);


            //long lIdImage  = MxFunction.findEntAtPoint(dX,dY,"IMAGE");
            long lIdImage = MxFunction.findEntAtPoint(dX, dY, "MxImageMark");
            if (lIdImage != 0) {
                String sGetVal = MxFunction.getxDataString(lIdImage, "MyData");

                Log.v("Find MxImage", decodeUnicode(sGetVal));

                this.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {


                                           new AlertDialog.Builder(MxCADAppActivity.this).setTitle("系统提示")//设置对话框标题

                                                   .setMessage("找到一个标记")//设置显示的内容

                                                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                                                       @Override

                                                       public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                                           // TODO Auto-generated method stub


                                                       }

                                                   }).show();//在按键响应事件中显示此对话框


                                       }
                                   }
                );
            } else {
                // long lId = MxFunction.findEntAtPoint(dX,dY,"TEXT,MTEXT");

                //if(lId != 0)
                {


                    long lImageId = MxFunction.drawImage("start.png", dX, dY, 30);

                    long lImageId2 = MxFunction.drawImage2("start.png", dX, dY, dX + 1000, dY + 3000);

                    MxFunction.setxDataString(lImageId, "MyData", "TestVal中文测试111");

                    String sGetVal = MxFunction.getxDataString(lImageId, "MyData");


                    this.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {


                                               new AlertDialog.Builder(MxCADAppActivity.this).setTitle("系统提示")//设置对话框标题

                                                       .setMessage("在图上绘了一个标记")//设置显示的内容

                                                       .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                                                           @Override

                                                           public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                                               // TODO Auto-generated method stub


                                                           }

                                                       }).show();//在按键响应事件中显示此对话框


                                           }
                                       }
                    );
                }
            }


        }
        return 0;
    }

    /**
     * 当移动，缩放视区如果超出当前大场景最大范围,会触发该事件。
     */
    @Override
    public void displayOutOfRange() {
        //Log.d("Test", "displayOutOfRange: ");
    }

    /**
     * 视显示比例发生了变化，会触发该事件。
     */
    @Override
    public void displayScaleChange() {

        ///Log.d("Test", "displayScaleChange");
    }

    /**
     * 异步方式的重生成操作完成
     */
    @Override
    public void regenComplete() {
        Log.d("Test", "regenComplete");
    }

    @Override
    public boolean createInterfaceLayout() {
        if(!m_isLoadAndroidLayoutUi)
            return  false;

        setContentView(R.layout.activity_open_cad);

        ResizeLayout  mFrameLayout = (ResizeLayout)this.findViewById(R.id.my_frame);

        Cocos2dxGLSurfaceView mGLSurfaceView = (Cocos2dxGLSurfaceView)this.findViewById(R.id.view_cad);
        Cocos2dxEditBox edittext =  (Cocos2dxEditBox)this.findViewById(R.id.my_edittext);

        initInterfaceLayout(mFrameLayout,edittext,mGLSurfaceView);
        return true;
    }
}
