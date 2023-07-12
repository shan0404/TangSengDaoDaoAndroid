package com.chat.uikit.group;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chat.base.act.WKWebViewActivity;
import com.chat.base.base.WKBaseActivity;
import com.chat.base.config.WKApiConfig;
import com.chat.base.config.WKConfig;
import com.chat.base.endpoint.EndpointCategory;
import com.chat.base.endpoint.EndpointManager;
import com.chat.base.endpoint.entity.WKAdvancedItemViewMenu;
import com.chat.base.entity.ChannelInfoEntity;
import com.chat.base.entity.WKChannelCustomerExtras;
import com.chat.base.entity.WKGroupType;
import com.chat.base.msgitem.WKChannelMemberRole;
import com.chat.base.net.HttpResponseCode;
import com.chat.base.utils.WKDialogUtils;
import com.chat.base.utils.SoftKeyboardUtils;
import com.chat.base.utils.singleclick.SingleClickUtil;
import com.chat.base.views.FullyGridLayoutManager;
import com.chat.base.views.BottomEntity;
import com.chat.uikit.R;
import com.chat.uikit.contacts.ChooseContactsActivity;
import com.chat.uikit.databinding.ActGroupDetailLayoutBinding;
import com.chat.uikit.group.adapter.GroupMemberAdapter;
import com.chat.uikit.group.service.GroupContract;
import com.chat.uikit.group.service.GroupModel;
import com.chat.uikit.group.service.GroupPresenter;
import com.chat.uikit.message.MsgModel;
import com.chat.uikit.user.UserDetailActivity;
import com.chat.uikit.utils.UIKitDialogUtils;
import com.xinbida.wukongim.WKIM;
import com.xinbida.wukongim.entity.WKChannel;
import com.xinbida.wukongim.entity.WKChannelExtras;
import com.xinbida.wukongim.entity.WKChannelMember;
import com.xinbida.wukongim.entity.WKChannelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 2019-11-30 10:24
 * 群组详情
 */
public class GroupDetailActivity extends WKBaseActivity<ActGroupDetailLayoutBinding> implements GroupContract.GroupView {
    private String groupNo;
    private GroupMemberAdapter groupMemberAdapter;
    private GroupPresenter groupPresenter;
    private int memberRole;
    private WKChannel groupChannel;
    private int groupType = 0;
    private WKChannelMember adminMember;
    private TextView titleTv;

    @Override
    protected ActGroupDetailLayoutBinding getViewBinding() {
        return ActGroupDetailLayoutBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setTitle(TextView titleTv) {
        this.titleTv = titleTv;
        titleTv.setText(R.string.chat_info);
    }

    @Override
    protected void initPresenter() {
        groupNo = getIntent().getStringExtra("channelId");
        groupPresenter = new GroupPresenter(this);
    }

    @Override
    protected boolean isShowTitleBottomView() {
        return false;
    }

    @Override
    protected void initView() {
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(this, 5);
        wkVBinding.userRecyclerView.setLayoutManager(layoutManager);
        groupMemberAdapter = new GroupMemberAdapter(new ArrayList<>());
        wkVBinding.userRecyclerView.setAdapter(groupMemberAdapter);
        wkVBinding.refreshLayout.setEnableOverScrollDrag(true);
        wkVBinding.refreshLayout.setEnableLoadMore(false);
        wkVBinding.refreshLayout.setEnableRefresh(false);
        View view = (View) EndpointManager.getInstance().invoke("msg_remind_view", new WKAdvancedItemViewMenu(groupNo, WKChannelType.GROUP, this));
        if (view != null) {
            wkVBinding.msgRemindLayout.removeAllViews();
            wkVBinding.msgRemindLayout.addView(view);
        }

        View findMsgView = (View) EndpointManager.getInstance().invoke("find_msg_view", new WKAdvancedItemViewMenu(groupNo, WKChannelType.GROUP, this));
        if (findMsgView != null) {
            wkVBinding.findContentView.setVisibility(View.VISIBLE);
            wkVBinding.findContentLayout.removeAllViews();
            wkVBinding.findContentLayout.addView(findMsgView);
        }
        View msgReceiptView = (View) EndpointManager.getInstance().invoke("msg_receipt_view", new WKAdvancedItemViewMenu(groupNo, WKChannelType.GROUP, this));
        if (msgReceiptView != null) {
            wkVBinding.msgReceiptLayout.removeAllViews();
            wkVBinding.msgReceiptLayout.addView(msgReceiptView);
        }
    }

    @Override
    protected void initListener() {
        SingleClickUtil.onSingleClick(wkVBinding.remarkLayout, view1 -> {
            Intent intent = new Intent(GroupDetailActivity.this, WKSetGroupRemarkActivity.class);
            intent.putExtra("groupNo", groupNo);
            startActivity(intent);
        });
        wkVBinding.showNickSwitchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                groupPresenter.updateGroupSetting(groupNo, "show_nick", b ? 1 : 0);
            }
        });
        wkVBinding.saveSwitchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                groupPresenter.updateGroupSetting(groupNo, "save", b ? 1 : 0);
            }
        });
        wkVBinding.chatPwdSwitchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                if (TextUtils.isEmpty(WKConfig.getInstance().getUserInfo().chat_pwd)) {
                    EndpointManager.getInstance().invoke("show_set_chat_pwd", null);
                    wkVBinding.chatPwdSwitchView.setChecked(!b);
                } else {
                    groupPresenter.updateGroupSetting(groupNo, "chat_pwd_on", b ? 1 : 0);
                }
            }
        });
        wkVBinding.muteSwitchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                groupPresenter.updateGroupSetting(groupNo, "mute", b ? 1 : 0);
            }
        });
        wkVBinding.stickSwitchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                groupPresenter.updateGroupSetting(groupNo, "top", b ? 1 : 0);
            }
        });
        groupMemberAdapter.addChildClickViewIds(R.id.handlerIv, R.id.userLayout);
        groupMemberAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            WKChannelMember groupMemberEntity = groupMemberAdapter.getItem(position);
            if (groupMemberEntity != null) {
                if (view1.getId() == R.id.handlerIv) {
                    //添加或删除
                    if (groupMemberEntity.memberUID.equalsIgnoreCase("-1")) {
                        //添加
                        String unSelectUidList = "";
                        List<WKChannelMember> list = WKIM.getInstance().getChannelMembersManager().getMembers(groupNo, WKChannelType.GROUP);
                        for (int i = 0, size = list.size(); i < size; i++) {
                            if (TextUtils.isEmpty(unSelectUidList)) {
                                unSelectUidList = list.get(i).memberUID;
                            } else unSelectUidList = unSelectUidList + "," + list.get(i).memberUID;
                        }

                        Intent intent = new Intent(GroupDetailActivity.this, ChooseContactsActivity.class);
                        intent.putExtra("unSelectUids", unSelectUidList);
                        intent.putExtra("isIncludeUids", false);
                        intent.putExtra("groupId", groupNo);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    } else {
                        //删除
                        Intent intent = new Intent(GroupDetailActivity.this, DeleteGroupMemberActivity.class);
                        intent.putExtra("groupId", groupNo);
                        startActivity(intent);
                    }
                } else if (view1.getId() == R.id.userLayout) {
                    Intent intent = new Intent(GroupDetailActivity.this, UserDetailActivity.class);
                    intent.putExtra("uid", groupMemberEntity.memberUID);
                    intent.putExtra("groupID", groupNo);
                    startActivity(intent);

                }
            }
        });
        SingleClickUtil.onSingleClick(wkVBinding.showAllMembersTv, view1 -> {
            Intent intent = new Intent(this, WKAllMembersActivity.class);
            intent.putExtra("channelID", groupNo);
            intent.putExtra("channelType", WKChannelType.GROUP);
            startActivity(intent);
        });
        SingleClickUtil.onSingleClick(wkVBinding.reportLayout, view1 -> {
            Intent intent = new Intent(this, WKWebViewActivity.class);
            intent.putExtra("channelType", WKChannelType.GROUP);
            intent.putExtra("channelID", groupNo);
            intent.putExtra("url", WKApiConfig.baseWebUrl + "report.html");
            startActivity(intent);
        });

        wkVBinding.exitBtn.setOnClickListener(v -> {
            List<BottomEntity> list = new ArrayList<>();
            list.add(new BottomEntity(getString(R.string.exit_group_tips), R.color.colorDark, false));
            list.add(new BottomEntity(getString(R.string.sure), R.color.red));
            WKDialogUtils.getInstance().showCommonBottomViewDialog(this, list, (position, bottomEntity) -> {
                if (position == 1) {
                    GroupModel.getInstance().exitGroup(groupNo, (code, msg) -> {
                        if (code == HttpResponseCode.success) {
                            WKIM.getInstance().getMsgManager().clear(groupNo, WKChannelType.GROUP);
                            MsgModel.getInstance().offsetMsg(groupNo, WKChannelType.GROUP, null);
                            WKIM.getInstance().getConversationManager().deleteMsg(groupNo, WKChannelType.GROUP);
                            EndpointManager.getInstance().invokes(EndpointCategory.wkExitChat, new WKChannel(groupNo, WKChannelType.GROUP));
                            finish();
                        } else showToast(msg);
                    });
                }
            });
        });
        SingleClickUtil.onSingleClick(wkVBinding.groupAvatarLayout, view1 -> {
            Intent intent = new Intent(this, GroupHeaderActivity.class);
            intent.putExtra("groupNo", groupNo);
            startActivity(intent);
        });
        SingleClickUtil.onSingleClick(wkVBinding.groupQrLayout, view1 -> {
            Intent intent = new Intent(this, GroupQrActivity.class);
            intent.putExtra("groupId", groupNo);
            startActivity(intent);
        });
        SingleClickUtil.onSingleClick(wkVBinding.groupManageLayout, view1 -> EndpointManager.getInstance().invoke("chat_show_group_manage_view", groupNo));
        wkVBinding.clearChatMsgLayout.setOnClickListener(v -> {
            List<BottomEntity> list = new ArrayList<>();
            list.add(new BottomEntity(getString(R.string.clear_chat_group_msg_dialog), R.color.color999, false));
            list.add(new BottomEntity(getString(R.string.sure)));
            WKDialogUtils.getInstance().showCommonBottomViewDialog(this, list, (position, bottomEntity) -> {
                if (position == 1) {
                    MsgModel.getInstance().offsetMsg(groupNo, WKChannelType.GROUP, null);
                    WKIM.getInstance().getMsgManager().clear(groupNo, WKChannelType.GROUP);
                    showToast(getString(R.string.cleared));
                }
            });

        });
        wkVBinding.inGroupNameLayout.setOnClickListener(v -> UIKitDialogUtils.getInstance().showUpdateNameInGroupDialog(this, groupNo, name -> {
            wkVBinding.inGroupNameTv.setText(name);
            SoftKeyboardUtils.getInstance().hideSoftKeyboard(this);
        }));
        SingleClickUtil.onSingleClick(wkVBinding.noticeLayout, view1 -> {
            if (groupChannel == null) return;
            String notice = "";
            if (groupChannel.localExtra != null && groupChannel.localExtra.containsKey(WKChannelExtras.notice)) {
                notice = (String) groupChannel.localExtra.get(WKChannelExtras.notice);
            }
            if (TextUtils.isEmpty(notice) && memberRole == WKChannelMemberRole.normal) {
                showSingleBtnDialog(getString(R.string.edit_group_notice));
                return;
            }
            Intent intent = new Intent(this, GroupNoticeActivity.class);
            intent.putExtra("groupNo", groupNo);
            intent.putExtra("oldNotice", notice);
            startActivity(intent);
        });
        SingleClickUtil.onSingleClick(wkVBinding.groupNameLayout, view1 -> {
            if (memberRole != WKChannelMemberRole.normal && groupChannel != null) {
                Intent intent = new Intent(this, UpdateGroupNameActivity.class);
                intent.putExtra("groupNo", groupNo);
                startActivity(intent);
            } else showSingleBtnDialog(getString(R.string.edit_group_notice));
        });
        //监听频道改变通知
        WKIM.getInstance().getChannelManager().addOnRefreshChannelInfo("group_detail_refresh_channel", (channel, isEnd) -> {
            if (channel != null) {
                if (channel.channelID.equalsIgnoreCase(groupNo) && channel.channelType == WKChannelType.GROUP) {
                    //同一个会话
                    groupChannel = channel;
                    setData();
                    setNotice();
                }
            }
        });
        //监听频道成员信息改变通知
        WKIM.getInstance().getChannelMembersManager().addOnRefreshChannelMemberInfo("group_detail_refresh_channel_member", (channelMember, isEnd) -> {
            if (channelMember != null) {
                if (channelMember.channelID.equalsIgnoreCase(groupNo) && channelMember.channelType == WKChannelType.GROUP) {

                    //本群内某个成员
                    for (int i = 0, size = groupMemberAdapter.getData().size(); i < size; i++) {
                        if (groupMemberAdapter.getData().get(i).memberUID.equalsIgnoreCase(channelMember.memberUID)) {
                            if (groupMemberAdapter.getData().get(i).role != channelMember.role) {
                                //如果有角色更改就重新获取成员
                                getMembers();
                            } else {
                                groupMemberAdapter.getData().get(i).memberName = channelMember.memberName;
                                groupMemberAdapter.getData().get(i).memberRemark = channelMember.memberRemark;
                                groupMemberAdapter.notifyItemChanged(i);
                            }
                            break;
                        }
                    }
                }
            }
        });
        //移除群成员监听
        WKIM.getInstance().getChannelMembersManager().addOnRemoveChannelMemberListener("group_detail_remove_channel_member", list -> {
            if (list != null && list.size() > 0) {
                for (int i = 0, size = list.size(); i < size; i++) {
                    for (int j = 0, len = groupMemberAdapter.getData().size(); j < len; j++) {
                        if (list.get(i).memberUID.equalsIgnoreCase(groupMemberAdapter.getData().get(j).memberUID)
                                && list.get(i).channelID.equals(groupMemberAdapter.getData().get(j).channelID)
                                && list.get(i).channelType == WKChannelType.GROUP) {
                            groupMemberAdapter.removeAt(j);
                            break;
                        }
                    }
                }
            }
            if (groupType == WKGroupType.normalGroup) {
                int count = WKIM.getInstance().getChannelMembersManager().getMemberCount(groupNo, WKChannelType.GROUP);
                titleTv.setText(String.format("%s(%s)", getString(R.string.chat_info), count));
            }
        });
        //添加群成员监听
        WKIM.getInstance().getChannelMembersManager().addOnAddChannelMemberListener("group_detail_add_channel_member", list -> {
            //这里这是演示sdk数据转成UI层数据。
            // 当然UI层也可以直接使用sdk的数据库
            List<WKChannelMember> tempList = new ArrayList<>();
            if (list != null && list.size() > 0) {
                for (int i = 0, size = list.size(); i < size; i++) {
                    if (list.get(i).channelID.equalsIgnoreCase(groupNo)
                            && list.get(i).channelType == WKChannelType.GROUP) {
                        tempList.add(list.get(i));
                    }
                }
                if (memberRole != WKChannelMemberRole.normal) {
                    groupMemberAdapter.addData(groupMemberAdapter.getData().size() - 2, tempList);
                } else
                    groupMemberAdapter.addData(groupMemberAdapter.getData().size() - 1, tempList);
                if (groupType == WKGroupType.normalGroup) {
                    int count = WKIM.getInstance().getChannelMembersManager().getMemberCount(groupNo, WKChannelType.GROUP);
                    titleTv.setText(String.format("%s(%s)", getString(R.string.chat_info), count));
                }
            }
        });
        //监听隐藏群管理入口
        EndpointManager.getInstance().setMethod("chat_hide_group_manage_view", object -> {
            wkVBinding.groupManageView.setVisibility(View.GONE);
            wkVBinding.groupManageLayout.setVisibility(View.GONE);
            return null;
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        super.initData();

        int count = WKIM.getInstance().getChannelMembersManager().getMemberCount(groupNo, WKChannelType.GROUP);
        titleTv.setText(getString(R.string.chat_info) + "(" + count + ")");
        groupChannel = WKIM.getInstance().getChannelManager().getChannel(groupNo, WKChannelType.GROUP);
        if (groupChannel != null) {
            if (groupChannel.remoteExtraMap != null) {
                if (groupChannel.remoteExtraMap.containsKey(WKChannelExtras.groupType)) {
                    Object groupTypeObject = groupChannel.remoteExtraMap.get(WKChannelExtras.groupType);
                    if (groupTypeObject instanceof Integer) {
                        groupType = (int) groupTypeObject;
                    }
                }
                if (groupType == WKGroupType.superGroup && groupChannel.remoteExtraMap.containsKey(WKChannelCustomerExtras.memberCount)) {
                    Object memberCountObject = groupChannel.remoteExtraMap.get(WKChannelCustomerExtras.memberCount);
                    if (memberCountObject instanceof Integer) {
                        int memberCount = (int) memberCountObject;
                        titleTv.setText(getString(R.string.chat_info) + "(" + memberCount + ")");
                    }
                }
            }

            setData();
            setNotice();
        }
        groupPresenter.getGroupInfo(groupNo);
        getMembers();
    }

    private void getMembers() {

        WKIM.getInstance().getChannelMembersManager().getWithPageOrSearch(groupNo, WKChannelType.GROUP, "", 1, 20, (list, b) -> {
            if (groupType == 0)
                resortData(list);
            else {
                if (b) {
                    resortData(list);
                }
            }
        });

//        List<WKChannelMember> list = WKIM.getInstance().getChannelMembersManager().getMembersWithPage(groupNo, WKChannelType.GROUP,1,20);

    }

    private void resortData(List<WKChannelMember> list) {
        WKChannelMember channelMember = WKIM.getInstance().getChannelMembersManager().getMember(groupNo, WKChannelType.GROUP, WKConfig.getInstance().getUid());
        int maxCount;
        if (channelMember != null && channelMember.role != WKChannelMemberRole.normal) {
            maxCount = 18;
        } else {
            maxCount = 19;
        }
        if (list != null) {
            List<WKChannelMember> temp = new ArrayList<>();
            for (int i = 0, size = Math.min(list.size(), maxCount); i < size; i++) {
                if (list.get(i).role == WKChannelMemberRole.admin) {
                    //群主或管理员
                    adminMember = list.get(i);
                    temp.add(0, list.get(i));
                } else temp.add(list.get(i));

                if (list.get(i).memberUID.equalsIgnoreCase(WKConfig.getInstance().getUid())) {
                    String name = list.get(i).memberRemark;
                    memberRole = list.get(i).role;
                    if (TextUtils.isEmpty(name))
                        name = list.get(i).memberName;
                    wkVBinding.inGroupNameTv.setText(name);
                }
            }
            //添加按钮
            WKChannelMember addUser = new WKChannelMember();
            addUser.memberUID = "-1";
            temp.add(addUser);
            if (memberRole != WKChannelMemberRole.normal) {
                //删除按钮
                WKChannelMember deleteUser = new WKChannelMember();
                deleteUser.memberUID = "-2";
                temp.add(deleteUser);
                wkVBinding.groupManageLayout.setVisibility(View.VISIBLE);
                wkVBinding.groupManageView.setVisibility(View.VISIBLE);
            }
            groupMemberAdapter.setList(temp);
            if (list.size() >= 18) {
                wkVBinding.showAllMembersTv.setVisibility(View.VISIBLE);
            } else wkVBinding.showAllMembersTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGroupInfo(ChannelInfoEntity groupEntity) {
        setData();
    }

    @Override
    public void onRefreshGroupSetting(String key, int value) {

    }

    @Override
    public void setQrData(int day, String qrCode, String expire) {

    }

    @Override
    public void setMyGroups(List<GroupEntity> list) {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String content = data.getStringExtra("content");
            wkVBinding.groupNoticeTv.setText(content);
        }
    }

    private void setNotice() {
        HashMap hashMap = groupChannel.localExtra;
        String notice = "";
        if (hashMap != null) {
            if (hashMap.containsKey(WKChannelExtras.notice)) {
                notice = (String) hashMap.get(WKChannelExtras.notice);
            }
        }
        if (!TextUtils.isEmpty(notice)) {
            wkVBinding.unsetNoticeLayout.setVisibility(View.GONE);
            wkVBinding.groupNoticeTv.setVisibility(View.VISIBLE);
            wkVBinding.groupNoticeTv.setText(notice);
        } else {
            wkVBinding.unsetNoticeLayout.setVisibility(View.VISIBLE);
            wkVBinding.groupNoticeTv.setVisibility(View.GONE);
        }

    }

    private void setData() {
        wkVBinding.nameTv.setText(groupChannel.channelName);
        wkVBinding.remarkTv.setText(groupChannel.channelRemark);
        wkVBinding.muteSwitchView.setChecked(groupChannel.mute == 1);
        wkVBinding.stickSwitchView.setChecked(groupChannel.top == 1);
        wkVBinding.saveSwitchView.setChecked(groupChannel.save == 1);
        wkVBinding.showNickSwitchView.setChecked(groupChannel.showNick == 1);

        if (groupChannel.remoteExtraMap != null && groupChannel.remoteExtraMap.containsKey(WKChannelExtras.chatPwdOn)) {
            Object chatPwdOn = groupChannel.remoteExtraMap.get(WKChannelExtras.chatPwdOn);
            if (chatPwdOn != null) {
                wkVBinding.chatPwdSwitchView.setChecked((int) chatPwdOn == 1);
            }
        }
        if (groupType == WKGroupType.superGroup && groupChannel.remoteExtraMap != null && groupChannel.remoteExtraMap.containsKey(WKChannelCustomerExtras.memberCount)) {
            Object memberCountObject = groupChannel.remoteExtraMap.get(WKChannelCustomerExtras.memberCount);
            if (memberCountObject instanceof Integer) {
                int memberCount = (int) memberCountObject;
                String content = String.format("%s(%s)", getString(R.string.chat_info), memberCount);
                titleTv.setText(content);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EndpointManager.getInstance().remove("chat_hide_group_manage_view");
        WKIM.getInstance().getChannelManager().removeRefreshChannelInfo("group_detail_refresh_channel");
        WKIM.getInstance().getChannelMembersManager().removeRefreshChannelMemberInfo("group_detail_refresh_channel_member");
        WKIM.getInstance().getChannelMembersManager().removeRemoveChannelMemberListener("group_detail_remove_channel_member");
        WKIM.getInstance().getChannelMembersManager().removeAddChannelMemberListener("group_detail_add_channel_member");
    }

}