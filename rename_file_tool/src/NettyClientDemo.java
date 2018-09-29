import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NettyClientDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                final JScrollPane outScroll = new JScrollPane();
                final JTextArea out = new JTextArea();
                outScroll.add(out);
                outScroll.setViewportView(out);
                outScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                outScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                JFrame f = new JFrame();
                GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWidths = new int[]{10, 100, 0, 10, 0};
                gridBagLayout.rowHeights = new int[]{10, 30, 30, 30, 30, 0};
                gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
                gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
                f.setLayout(gridBagLayout);

                final JLabel ruleLabel = new JLabel("命名规则：");
                GridBagConstraints gbc_ruleLabel = new GridBagConstraints();
                gbc_ruleLabel.gridx = 1;
                gbc_ruleLabel.gridy = 1;
                gbc_ruleLabel.anchor = GridBagConstraints.EAST;
                f.add(ruleLabel, gbc_ruleLabel);

                final JTextField ruleTF = new JTextField();
                ruleTF.setText("测试人员%num-YCKJ%num-0000.jpg");
                GridBagConstraints gbc_ruleTF = new GridBagConstraints();
                gbc_ruleTF.gridx = 2;
                gbc_ruleTF.gridy = 1;
                gbc_ruleTF.fill = GridBagConstraints.BOTH;
                f.add(ruleTF, gbc_ruleTF);

                JLabel folderLabel = new JLabel("文件夹路径：");
                GridBagConstraints gbc_folderLabel = new GridBagConstraints();
                gbc_folderLabel.gridx = 1;
                gbc_folderLabel.gridy = 2;
                gbc_folderLabel.anchor = GridBagConstraints.EAST;
                gbc_folderLabel.insets = new Insets(10, 0, 0, 0);
                f.add(folderLabel, gbc_folderLabel);

                final JTextField folderTF = new JTextField();
                folderTF.setText("");
                GridBagConstraints gbc_folderTF = new GridBagConstraints();
                gbc_folderTF.gridx = 2;
                gbc_folderTF.gridy = 2;
                gbc_folderTF.fill = GridBagConstraints.BOTH;
                gbc_folderTF.insets = new Insets(10, 0, 0, 0);
                f.add(folderTF, gbc_folderTF);

                JLabel progressLabel = new JLabel("重命名进度：");
                GridBagConstraints gbc_progressLabel = new GridBagConstraints();
                gbc_progressLabel.gridx = 1;
                gbc_progressLabel.gridy = 3;
                gbc_progressLabel.anchor = GridBagConstraints.EAST;
                gbc_progressLabel.insets = new Insets(10, 0, 0, 0);
                f.add(progressLabel, gbc_progressLabel);


                final JLabel progress = new JLabel("0/0/0");
                GridBagConstraints gbc_progress = new GridBagConstraints();
                gbc_progress.gridx = 2;
                gbc_progress.gridy = 3;
                gbc_progress.fill = GridBagConstraints.BOTH;
                gbc_progress.insets = new Insets(10, 0, 0, 0);
                f.add(progress, gbc_progress);

                final JLabel tip = new JLabel("使用说明：");
                GridBagConstraints gbc_tip = new GridBagConstraints();
                gbc_tip.gridx = 1;
                gbc_tip.gridy = 4;
                gbc_tip.fill = GridBagConstraints.EAST;
                gbc_tip.insets = new Insets(10, 0, 0, 0);
                f.add(tip, gbc_tip);

                final JLabel tip2 = new JLabel("<html>%num为替换符号，重命名会使用连续的数字来替换%num,命名规则中其他内容会保持不变<br>重命名进度为已完成数/失败数/总数</html>");
                GridBagConstraints gbc_tip2 = new GridBagConstraints();
                gbc_tip2.gridx = 2;
                gbc_tip2.gridy = 4;
                gbc_tip2.fill = GridBagConstraints.BOTH;
                gbc_tip2.insets = new Insets(10, 0, 0, 0);
                f.add(tip2, gbc_tip2);

                final JButton startBtn = new JButton("开始");
                GridBagConstraints gbc_startBtn = new GridBagConstraints();
                gbc_startBtn.gridx = 2;
                gbc_startBtn.gridy = 5;
                gbc_startBtn.anchor = GridBagConstraints.EAST;
                gbc_startBtn.insets = new Insets(10, 0, 0, 0);
                f.add(startBtn, gbc_startBtn);

                startBtn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try{
                            startBtn.setEnabled(false);
                            java.io.File f = new java.io.File(folderTF.getText());
                            if(f.exists() && f.isDirectory()){
                                startRename(f, ruleTF.getText(), progress);
                            }else{
                                JOptionPane.showMessageDialog(null, "指定的文件路径不存在或者不是文件夹！");
                                startBtn.setEnabled(true);
                            }
                        }catch(Exception ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                            startBtn.setEnabled(true);
                        }
                    }
                });

                // 加上这一句就可以显示一个仅有关闭，最小化，最大化的按钮的Frame了
                f.setVisible(true);
                // 再加上这一句就可以显示一个在左上角，拥有指定大小的Frame了
                f.setSize(700, 230);
                // 再加上这一句就可以把Frame放在最中间了
                f.setLocationRelativeTo(null);
                // 如果没有这一句，在点击关闭Frame的时候程序其实还是在执行状态中的，加上这一句才算是真正的把资源释放掉了
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }

            private void startRename(final File f, final String rule, final JLabel label) {
                new SwingWorker<Integer, String>() {

                    @Override
                    protected void process(List<String> chunks) {
                        if(chunks != null && !chunks.isEmpty()){
                            label.setText(chunks.get(0));
                        }
                    }

                    @Override
                    protected Integer doInBackground() throws Exception {

                        final AtomicLong successCount = new AtomicLong(0);
                        final AtomicLong failCount = new AtomicLong(0);

                        publish("统计文件数据中...");

                        final File[] fs = f.listFiles();

                        publish("0/0/" + fs.length);

                        //开启线程核数2倍的线程
                        int threadCount = Runtime.getRuntime().availableProcessors() * 2 - 1;
                        int partCount = fs.length/threadCount;
                        //如果分块，每个块
                        if (partCount == 0){
                            Executors.newFixedThreadPool(1).submit(new RenameFileRunnable(fs, 0, new Callback() {
                                @Override
                                public void success() {
                                    publish(successCount.incrementAndGet() + "/" + failCount.get() + "/" + fs.length);
                                }

                                @Override
                                public void fail() {
                                    publish(successCount.get() + "/" + failCount.incrementAndGet() + "/" + fs.length);
                                }
                            }, rule));
                        }
                        //
                        else{
                            final ExecutorService service = Executors.newFixedThreadPool(threadCount);
                            for(int i=0; i<threadCount; i++){
                                int startIdx = partCount*i;
                                File[] part = null;
                                if(i == threadCount-1){
                                    part = new File[fs.length-partCount*(threadCount-1)];
                                }else{
                                    part = new File[partCount];
                                }
                                System.arraycopy(fs, i*partCount, part, 0, part.length);
                                service.submit(new RenameFileRunnable(part, startIdx, new Callback() {
                                    @Override
                                    public void success() {
                                        publish(successCount.incrementAndGet() + "/" + failCount.get() + "/" + fs.length);
                                    }

                                    @Override
                                    public void fail() {
                                        publish(successCount.get() + "/" + failCount.incrementAndGet() + "/" + fs.length);
                                    }
                                }, rule));
                            }
                        }
                        return null;
                    }



                }.execute();



            }
        });

    }
}
