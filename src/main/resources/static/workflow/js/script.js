document.addEventListener('DOMContentLoaded', function () {
    // DOM element references
    const tenantForm = document.getElementById('tenantForm');
    const tenantIdInput = document.getElementById('tenantId');
    const proceedBtn = document.getElementById('proceedBtn');
    const formLoader = document.getElementById('formLoader');
    const authModal = document.getElementById('authModal');
    const mainContent = document.getElementById('mainContent');
    const workflowList = document.getElementById('workflowList');
    const workflowTableCard = document.getElementById('workflowTableCard');
    const emptyState = document.getElementById('emptyState');
    const createTemplateBtn = document.getElementById('createTemplateBtn');
    const createFirstTemplateBtn = document.getElementById('createFirstTemplateBtn');
    const newTemplateModal = document.getElementById('newTemplateModal');
    const templateForm = document.getElementById('templateForm');
    const submitBtn = document.getElementById('submitTemplate');

    // Global variables
    let editor = null;
    let tenantId = localStorage.getItem('tenantId');

    // Initialize
    if (tenantId) {
        showMainContent();
        loadWorkflowTemplates();
    }

    // Enable/disable proceed button based on input
    tenantIdInput.addEventListener('input', function () {
        proceedBtn.disabled = !this.value.trim();
    });

    // Set initial state of proceed button
    proceedBtn.disabled = !tenantIdInput.value.trim();

    // Handle tenant form submission
    tenantForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const inputTenantId = tenantIdInput.value.trim();

        if (inputTenantId) {
            showLoading(true);
            // Simulate API call delay
            setTimeout(() => {
                localStorage.setItem('tenantId', inputTenantId);
                tenantId = inputTenantId;
                showMainContent();
                loadWorkflowTemplates();
            }, 500);
        }
    });

    // Handle template form submission
    if (templateForm) {
        templateForm.addEventListener('submit', handleTemplateSubmit);
    }

    // Handle submit button click
    if (submitBtn) {
        submitBtn.addEventListener('click', function (e) {
            e.preventDefault();
            if (templateForm) {
                templateForm.dispatchEvent(new Event('submit'));
            }
        });
    }

    // Handle create template buttons
    if (createTemplateBtn) {
        createTemplateBtn.addEventListener('click', openTemplateModal);
    }

    if (createFirstTemplateBtn) {
        createFirstTemplateBtn.addEventListener('click', openTemplateModal);
    }

    // Initialize CodeMirror when modal is shown
    newTemplateModal.addEventListener('shown.bs.modal', function () {
        const editorElement = document.getElementById('editor');

        if (editorElement && !editor) {
            // Clear any existing content
            editorElement.innerHTML = '';

            // Create a new textarea for CodeMirror
            const textarea = document.createElement('textarea');
            textarea.id = 'templateEditor';
            editorElement.appendChild(textarea);

            // Initialize CodeMirror
            editor = CodeMirror.fromTextArea(textarea, {
                mode: 'htmlmixed',
                theme: 'monokai',
                lineNumbers: true,
                lineWrapping: true,
                autoCloseTags: true,
                matchTags: { bothTags: true },
                extraKeys: { "Ctrl-Space": "autocomplete" },
                viewportMargin: Infinity,
                autoRefresh: true
            });

            // Set initial value
            editor.setValue('<!-- Your HTML template here -->\n<!-- Use placeholders like {{workflowId}}, {{workflowName}}, {{workflowDescription}}, {{requesterServiceName}}, {{approveLink}}, {{rejectLink}} -->\n');

            // Sync CodeMirror with hidden textarea
            editor.on('change', (cm) => {
                document.getElementById('templateContent').value = cm.getValue();
            });

            // Force refresh the editor
            setTimeout(() => editor.refresh(), 0);
        } else if (editor) {
            editor.refresh();
        }
    });

    // Reset form when modal is hidden
    newTemplateModal.addEventListener('hidden.bs.modal', function () {
        if (templateForm) {
            templateForm.reset();
        }
        if (editor) {
            editor.setValue('<!-- Your HTML template here -->\n<!-- Use placeholders like {{workflowId}}, {{workflowName}}, {{workflowDescription}}, {{requesterServiceName}}, {{approveLink}}, {{rejectLink}} -->\n');
        }
    });

    // Helper Functions
    function showLoading(isLoading) {
        if (isLoading) {
            proceedBtn.style.display = 'none';
            formLoader.style.display = 'block';
        } else {
            proceedBtn.style.display = 'block';
            formLoader.style.display = 'none';
        }
    }

    function showMainContent() {
        authModal.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => {
            authModal.style.display = 'none';
            mainContent.style.display = 'block';
            document.body.style.overflow = 'auto';
        }, 300);
    }

    function openTemplateModal(e) {
        e.preventDefault();
        const modal = new bootstrap.Modal(newTemplateModal);
        modal.show();
    }

    function loadWorkflowTemplates() {
        fetch('/v1/workflow/templates', {
            headers: {
                'Content-Type': 'application/json',
                'tenant-id': tenantId
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to load templates');
                }
                return response.json();
            })
            .then(templates => {
                displayTemplates(templates);
            })
            .catch(error => {
                console.error('Error:', error);
                // Show empty state on error
                displayTemplates([]);
            });
    }

    function displayTemplates(templates) {
        if (!templates || templates.length === 0) {
            // Show empty state
            workflowTableCard.style.display = 'none';
            emptyState.classList.remove('d-none');
            // Hide header create button when empty
            if (createTemplateBtn) {
                createTemplateBtn.style.display = 'none';
            }
            return;
        }

        // Show table
        workflowTableCard.style.display = 'block';
        emptyState.classList.add('d-none');
        // Show header create button when templates exist
        if (createTemplateBtn) {
            createTemplateBtn.style.display = 'inline-flex';
        }

        // Populate table
        workflowList.innerHTML = templates.map(template => {
            const createdDate = template.createdAt
                ? new Date(template.createdAt).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                })
                : 'N/A';

            return `
                <tr>
                    <td class="ps-4">
                        <div>
                            <h6 class="mb-1">${escapeHtml(template.name || 'Untitled Template')}</h6>
                            <small class="text-muted">${escapeHtml(template.description || 'No description provided')}</small>
                        </div>
                    </td>
                    <td>
                        <code>${escapeHtml(template.workflowId)}</code>
                    </td>
                    <td>
                        <span class="badge bg-primary">${escapeHtml(template.channel || 'EMAIL')}</span>
                    </td>
                    <td>
                        <small class="text-muted">${createdDate}</small>
                    </td>
                </tr>
            `;
        }).join('');
    }

    async function handleTemplateSubmit(e) {
        e.preventDefault();

        const form = e.target.tagName === 'FORM' ? e.target : document.getElementById('templateForm');
        if (!form) {
            console.error('Form not found');
            return;
        }

        const submitBtn = document.getElementById('submitTemplate') || form.querySelector('button[type="submit"]');
        if (!submitBtn) {
            console.error('Submit button not found');
            return;
        }

        const originalBtnText = submitBtn.innerHTML;
        const spinner = submitBtn.querySelector('.spinner-border');

        try {
            // Show loading state
            submitBtn.disabled = true;
            if (spinner) spinner.classList.remove('d-none');
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creating...';

            // Get form values
            const templateData = {
                workflowId: document.getElementById('workflowId')?.value?.trim() || '',
                name: document.getElementById('workflowName')?.value?.trim() || '',
                description: document.getElementById('description')?.value?.trim() || '',
                channelType: document.querySelector('input[name="channelType"]:checked')?.value || 'EMAIL',
                htmlTemplate: editor?.getValue() || ''
            };

            // Validation
            if (!templateData.workflowId || !templateData.name) {
                throw new Error('Please fill in all required fields');
            }

            if (!templateData.htmlTemplate || templateData.htmlTemplate.trim().length === 0) {
                throw new Error('Please provide an HTML template');
            }

            // Send request
            const response = await fetch('/v1/workflow/template', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'tenant-id': localStorage.getItem('tenantId') || ''
                },
                body: JSON.stringify(templateData)
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to create template');
            }

            // Close modal and refresh list
            const modal = bootstrap.Modal.getInstance(newTemplateModal);
            if (modal) modal.hide();

            // Reset form
            form.reset();
            if (editor) {
                editor.setValue('<!-- Your HTML template here -->\n<!-- Use placeholders like {{workflowId}}, {{workflowName}}, {{workflowDescription}}, {{requesterServiceName}}, {{approveLink}}, {{rejectLink}} -->\n');
            }

            // Reload templates
            await loadWorkflowTemplates();

            // Show success message (optional)
            showNotification('Template created successfully!', 'success');

        } catch (error) {
            console.error('Error:', error);
            showNotification(error.message || 'Failed to create template', 'error');
        } finally {
            // Reset button state
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        }
    }

    // Helper function to escape HTML
    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text ? String(text).replace(/[&<>"']/g, m => map[m]) : '';
    }

    // Optional: Show notification function
    function showNotification(message, type = 'info') {
        // You can implement a toast notification here
        // For now, using alert as fallback
        if (type === 'error') {
            alert(message);
        } else {
            console.log(message);
        }
    }
});
