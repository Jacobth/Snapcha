package shutdown.chalmergps.jacobth.snapcha;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public interface PanelListener {
    /**
     * Called when a panel's position changes.
     * @param toolbar Toolbar view that is now open
     * @param panelView The child view that was moved
     * @param slideOffset The new offset of this panel within its range, from 0-1
     */
    public void onPanelSlide(Button toolbar, View panelView, float slideOffset);

    /**
     * Called when a panel has settled in a completely open state.
     * The panel is interactive at this point.
     *
     * @param toolbar Toolbar view that is now open
     * @param panelView Panel view that is now open
     */
    public void onPanelOpened(Button toolbar, View panelView);

    /**
     * Called when a panel has settled in a completely closed state.
     *
     * @param toolbar Toolbar view that is now closed
     * @param panelView Panel view that is now closed
     */
    public void onPanelClosed(Button toolbar, View panelView);
}
