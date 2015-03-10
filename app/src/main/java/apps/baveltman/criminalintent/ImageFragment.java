package apps.baveltman.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Fragment to show crime image taken in full screen
 */
public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH =
            "apps.baveltman.image_path";

    private ImageView mImageView;

    /**
     * Use onCreateView as an optimization since we do not need all of the buttons associated with a Dialog
     * @param inflater
     * @param parent
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
        mImageView.setImageDrawable(image);
        return mImageView;
    }


    /**
     * release memory resources once this fragment is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }

    /**
     * used to instantiate a new instance of this fragment and pass it as an argument
     * @param imagePath
     * @return
     */
    public static ImageFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }
}
