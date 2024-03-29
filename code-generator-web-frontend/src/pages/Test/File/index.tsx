import { COS_HOST } from '@/constants';
import { testFileDownloadUsingGet, testFileUploadUsingPost } from '@/services/backend/fileController';
import { InboxOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Divider, Flex, message, Upload, UploadProps } from 'antd';
import { saveAs } from 'file-saver';
import React, { useState } from 'react';

const { Dragger } = Upload;

const TestFilePage: React.FC = () => {
  const [value, setValue] = useState<string>();

  const props: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    customRequest: async (fileObj: any) => {
      try {
        const res = await testFileUploadUsingPost({}, fileObj.file);
        fileObj.onSuccess(res.data);
        setValue(res.data);
      } catch (e: any) {
        message.error('上传失败，' + e.message);
        fileObj.onError(e);
      }
    },
    onRemove: () => {
      setValue(undefined);
    },
  };
  return (
    <PageContainer>
      <Flex gap={16}>
        <Card title="文件上传">
          <Dragger {...props}>
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">Click or drag file to this area to upload</p>
            <p className="ant-upload-hint">
              Support for a single or bulk upload. Strictly prohibited from uploading company data or
              other banned files.
            </p>
          </Dragger>
        </Card>
        <Card title="文件下载">
          文件地址: {COS_HOST + value}
          <Divider />
          <img src={COS_HOST + value} alt="" height={200} />
          <Divider />
          <Button
            onClick={async () => {
              const blob = await testFileDownloadUsingGet(
                {
                  filepath: value,
                },
                {
                  responseType: 'blob',
                },
              );
              const fullPath = COS_HOST + value;
              saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
            }}
          >
            点击下载文件
          </Button>
        </Card>
      </Flex>
    </PageContainer>
  );
};
export default TestFilePage;
