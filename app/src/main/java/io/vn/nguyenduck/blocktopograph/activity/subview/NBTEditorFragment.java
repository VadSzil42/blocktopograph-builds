package io.vn.nguyenduck.blocktopograph.activity.subview;

import static io.vn.nguyenduck.blocktopograph.utils.Utils.translateEscapes;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.world.WorldPreLoader;
import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.tags.ByteTag;
import io.vn.nguyenduck.nbt.tags.CompoundTag;
import io.vn.nguyenduck.nbt.tags.DoubleTag;
import io.vn.nguyenduck.nbt.tags.FloatTag;
import io.vn.nguyenduck.nbt.tags.IntTag;
import io.vn.nguyenduck.nbt.tags.ListTag;
import io.vn.nguyenduck.nbt.tags.LongTag;
import io.vn.nguyenduck.nbt.tags.ShortTag;
import io.vn.nguyenduck.nbt.tags.StringTag;

public class NBTEditorFragment extends Fragment {

    private WorldPreLoader world;

    public void setWorld(WorldPreLoader world) {
        this.world = world;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nbt_editor, container, false);

        FrameLayout frameLayout = v.findViewById(R.id.nbt_editor_frame);

        CompoundTag<?> compound = world.getLevelData();

        TreeNode superRoot = TreeNode.root();
        TreeNode root = new TreeNode(new TagInfo(null, compound));
        RootNodeHolder rootNodeHolder = new RootNodeHolder(requireContext());

        superRoot.addChild(root);
        root.setExpanded(true);
        root.setSelectable(false);

        root.setViewHolder(rootNodeHolder);

        for (Tag<?> child : compound) {
            NBTNodeHolder holder = new NBTNodeHolder(requireContext());
            TreeNode childNode = new TreeNode(new TagInfo(compound, child)).setViewHolder(holder);
            root.addChild(childNode);
        }

        AndroidTreeView tree = new AndroidTreeView(requireActivity(), superRoot);
        tree.setUse2dScroll(true);

        View treeView = tree.getView();
        treeView.setScrollContainer(true);

        frameLayout.addView(treeView);

        FloatingActionButton saveBtn = frameLayout.findViewById(R.id.fab_save_nbt);

        saveBtn.setOnClickListener(view -> {
            try {
                world.saveLevelData();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return v;
    }

    public static class RootNodeHolder extends TreeNode.BaseNodeViewHolder<TagInfo> {

        public final Context context;
        private final LayoutInflater inflater;

        public RootNodeHolder(Context context) {
            super(context);
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View createNodeView(TreeNode node, TagInfo value) {
            View tagView = inflater.inflate(R.layout.tag_root_layout, null, false);
            TextView tagName = tagView.findViewById(R.id.tag_name);
            tagName.setText("Root");
            return tagView;
        }

        @Override
        public int getContainerStyle() {
            return R.style.TreeNodeStyle;
        }
    }

    public static class NBTNodeHolder extends TreeNode.BaseNodeViewHolder<TagInfo> {

        private final LayoutInflater inflater;

        public NBTNodeHolder(Context context) {
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View createNodeView(TreeNode node, TagInfo value) {
            Tag<?> tag = value.thisTag;

            int layoutId;

            switch (tag.getType()) {
                case COMPOUND -> {
                    layoutId = R.layout.tag_compound_layout;
                    for (Tag<?> child : (CompoundTag<?>) tag) {
                        node.addChild(new TreeNode(new TagInfo(tag, child)).setViewHolder(new NBTNodeHolder(context)));
                    }
                }
                case LIST -> {
                    layoutId = R.layout.tag_list_layout;
                    for (Tag<?> child : (ListTag<?>) tag) {
                        node.addChild(new TreeNode(new TagInfo(tag, child)).setViewHolder(new NBTNodeHolder(context)));
                    }
                }
                case BYTE -> {
                    String name = tag.getName();
                    name = name == null ? "" : name.toLowerCase();
                    layoutId = name.startsWith("has") || name.startsWith("is") ?
                            R.layout.tag_boolean_layout :
                            R.layout.tag_byte_layout;
                }
                case SHORT -> layoutId = R.layout.tag_short_layout;
                case INT -> layoutId = R.layout.tag_int_layout;
                case LONG -> layoutId = R.layout.tag_long_layout;
                case FLOAT -> layoutId = R.layout.tag_float_layout;
                case DOUBLE -> layoutId = R.layout.tag_double_layout;
                case STRING -> layoutId = R.layout.tag_string_layout;
                default -> layoutId = R.layout.tag_default_layout;
            }

            View tagView = inflater.inflate(layoutId, null, false);
            TextView tagName = tagView.findViewById(R.id.tag_name);
            tagName.setText(tag.getName());

            View tagValueView = tagView.findViewById(R.id.tag_value);
            View tagValueEdit = tagView.findViewById(R.id.tag_value_edit);

            switch (layoutId) {
                case R.layout.tag_boolean_layout -> {
                    CheckBox checkBox = (CheckBox) tagValueEdit;
                    ByteTag byteTag = (ByteTag) tag;
                    checkBox.setChecked(byteTag.getValue() == 1);
                    checkBox.setOnCheckedChangeListener((view, isChecked) -> {
                        byteTag.setValue(isChecked ? (byte) 1 : (byte) 0);
                    });
                }
                case R.layout.tag_byte_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    ByteTag tagValue = (ByteTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                int value = Integer.parseInt(sValue);
                                if (value < 0 || value > 0xff)
                                    throw new NumberFormatException("No unsigned byte.");
                                tagValue.setValue((byte) value);
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_short_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    ShortTag tagValue = (ShortTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                tagValue.setValue(Short.valueOf(sValue));
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_int_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    IntTag tagValue = (IntTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                tagValue.setValue(Integer.valueOf(sValue));
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_long_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    LongTag tagValue = (LongTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                tagValue.setValue(Long.valueOf(sValue));
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_float_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    FloatTag tagValue = (FloatTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                tagValue.setValue(Float.valueOf(sValue));
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_double_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    DoubleTag tagValue = (DoubleTag) tag;
                    textView.setText(String.valueOf(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                tagValue.setValue(Double.valueOf(sValue));
                                textView.setText(sValue);
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                }
                case R.layout.tag_string_layout -> {
                    var textView = (TextView) tagValueView;
                    var editText = (EditText) tagValueEdit;
                    StringTag tagValue = (StringTag) tag;
                    textView.setText(translateEscapes(tagValue.getValue()));
                    editText.setText(String.valueOf(tagValue.getValue()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            tagValue.setValue(s.toString());
                            textView.setText(s.toString());
                        }
                    });
                }
            }

            InputMethodManager imm = context.getSystemService(InputMethodManager.class);

            if (tagValueView instanceof TextView textView) {
                textView.setOnClickListener(view -> {
                    textView.setVisibility(View.GONE);
                    if (tagValueEdit instanceof EditText editText) {
                        editText.setVisibility(View.VISIBLE);
                        editText.requestFocus();
                        editText.setText(textView.getText());
                        editText.setSelection(editText.getText().length());
                    }
                });
            }

            if (tagValueEdit instanceof EditText editText) {
                editText.setOnFocusChangeListener((v, f) -> {
                    if (!f) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        editText.clearFocus();
                        editText.setVisibility(View.GONE);
                        assert tagValueView != null;
                        tagValueView.setVisibility(View.VISIBLE);
                    }
                });
            }

            return tagView;
        }

        @Override
        public int getContainerStyle() {
            return R.style.TreeNodeStyle;
        }
    }

    public static class TagInfo {
        public Tag<?> parent;
        public Tag<?> thisTag;

        public TagInfo(Tag<?> parent, Tag<?> thisTag) {
            this.parent = parent;
            this.thisTag = thisTag;
        }
    }
}